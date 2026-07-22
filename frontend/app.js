/**
 * ============================================================================
 * UAV COMMAND & CONTROL (C2) - FRONTEND CORE LOGIC
 * ============================================================================
 * Architecture : Event-Driven & Real-Time (WebSocket/STOMP)
 * Map Engine   : Leaflet.js
 * Pattern      : Modular Configuration & Guard Clauses
 * ============================================================================
 */

/**
 * APPLICATION CONFIGURATION
 * Centralized registry for all magic values, endpoints, and UI constraints.
 * Modifying values here will safely cascade throughout the application.
 */
const SABITLER = {
    API: {
        FILO: 'http://localhost:8080/api/drones/filo',
        EKLE: 'http://localhost:8080/api/drones/ekle',
        BASE: 'http://localhost:8080/api/drones' // Core path for dynamic endpoints (e.g., RTB)
    },
    WS: {
        URL: 'http://localhost:8080/ws-radar',
        TOPIC: '/topic/drones'
    },
    HARITA: {
        MERKEZ: [39.0, 35.0], // Default focus on Turkey
        ZOOM: 6,
        TILE_URL: 'https://{s}.basemaps.cartocdn.com/dark_all/{z}/{x}/{y}{r}.png',
        GEOJSON_URL: 'https://nominatim.openstreetmap.org/search?country=Turkey&polygon_geojson=1&format=json',
        KUYRUK_UZUNLUGU: 15 // Max coordinate points kept in memory for comet tail effect
    },
    RENKLER: {
        NORMAL: '#00ff41', // Tactical Green
        TEHLIKE: '#ff003c', // Alert Red (Bingo Fuel / RTB)
        BAKIM: 'gray'      // Offline / Maintenance
    },
    US_KOORDINAT: { ENLEM: 39.92077, BOYLAM: 32.85411 } // Origin Base (Ankara HQ)
};

// ============================================================================
// MAP INITIALIZATION & UI RENDERING
// ============================================================================

const map = L.map('map').setView(SABITLER.HARITA.MERKEZ, SABITLER.HARITA.ZOOM);

L.tileLayer(SABITLER.HARITA.TILE_URL, {
    attribution: '&copy; C2 Taktik Sistem',
    subdomains: 'abcd',
    maxZoom: 19
}).addTo(map);

/**
 * Fetches and draws the tactical border of the operational area (Turkey) using GeoJSON.
 * Implements a subtle green tint to simulate a radar sweep area.
 */
async function taktikSinirlariCiz() {
    try {
        const response = await fetch(SABITLER.HARITA.GEOJSON_URL);
        const data = await response.json();
        const turkiyeGeoJSON = data[0].geojson;

        L.geoJSON(turkiyeGeoJSON, {
            style: {
                color: SABITLER.RENKLER.NORMAL,       
                weight: 2,              
                opacity: 0.6,           
                fillColor: SABITLER.RENKLER.NORMAL,   
                fillOpacity: 0.05       
            }
        }).addTo(map);
    } catch (error) {
        console.error("[MAP ERROR] Failed to fetch tactical borders:", error);
    }
}

taktikSinirlariCiz();

// Memory states for rendering interpolation and trajectory tracking
let ucakIkonlari = {};
let ucakIzleri = {}; 

/**
 * Generates dynamic SVG icons based on UAV status and heading.
 * @param {string} durum - Operational status (e.g., IN_MISSION, RETURNING)
 * @param {number} aci - Calculated heading angle in degrees
 */
const getIhaIkonu = (durum, aci) => {
    const renk = (durum === 'RETURNING') ? SABITLER.RENKLER.TEHLIKE : SABITLER.RENKLER.NORMAL;
    return L.divIcon({
        className: 'custom-div-icon',
        html: `<div style="transform: rotate(${aci}deg); color: ${renk}; font-size: 20px;">✈</div>`,
        iconSize: [20, 20],
        iconAnchor: [10, 10]
    });
};

// ============================================================================
// DATA FETCHING & DATA BINDING
// ============================================================================

/**
 * Initial payload fetch to populate the UI before the WebSocket connection is established.
 */
async function filoyuGetir() {
    try {
        const response = await fetch(SABITLER.API.FILO);
        const dronelar = await response.json();
        
        ekranaCiz(dronelar);
        haritayiGuncelle(dronelar);
    } catch (error) {
        console.error("[API ERROR] Initial fleet synchronization failed:", error);
    }
}

/**
 * Updates the sidebar DOM elements.
 * Rebuilds the drone list cards based on real-time telemetry.
 */
function ekranaCiz(dronelar) {
    const listeDiv = document.getElementById('drone-list');
    listeDiv.innerHTML = ''; 

    dronelar.forEach(drone => {
        let durumSinifi = '';
        let renk = SABITLER.RENKLER.NORMAL; 
        
        // Status evaluation for UI coloring
        if (drone.durum === 'RETURNING') {
            durumSinifi = 'alert-danger'; 
            renk = SABITLER.RENKLER.TEHLIKE;
        } else if (drone.durum === 'MAINTENANCE') {
            renk = SABITLER.RENKLER.BAKIM;
        }

        // Action button visibility control (Only active missions can be recalled)
        let butonGorunurluk = (drone.durum === 'IN_MISSION') ? '' : 'd-none';

        const kart = document.createElement('div');
        kart.className = `drone-card ${durumSinifi}`; 
        kart.innerHTML = `
            <div><strong>KOD    :</strong> ${drone.cagriKodu}</div>
            <div><strong>MODEL  :</strong> ${drone.model}</div>
            <div><strong>SARJ   :</strong> %${drone.bataryaSeviyesi.toFixed(1)}</div>
            <div><strong>IRTIFA :</strong> ${drone.irtifa} ft</div>
            <div><strong>HIZ    :</strong> ${drone.hiz} knt</div>
            <div><strong>DURUM  :</strong> <span style="color: ${renk}">${drone.durum}</span></div>
            <button class="btn-rtb ${butonGorunurluk}" onclick="manuelRtbEmriVer(${drone.id})">[ DIREKTIF: USSE DON ]</button>
        `;
        listeDiv.appendChild(kart);
    });
}

/**
 * Operator Override Command: Forces a UAV to abort mission and return to base.
 * @param {number} droneId - Unique identifier of the target UAV
 */
async function manuelRtbEmriVer(droneId) {
    try {
        await fetch(`${SABITLER.API.BASE}/${droneId}/rtb`, { method: 'POST' });
        console.log(`[C2 COMMAND] RTB signal sent to Drone ID: ${droneId}`);
        filoyuGetir(); // Immediate UI update to reflect the operator command
    } catch (error) {
        console.error("[C2 COMMAND ERROR] Failed to transmit RTB directive!", error);
    }
}

/**
 * Core rendering loop for Leaflet map markers and trajectory polylines.
 * Calculates heading (Math.atan2) and manages the comet tail memory array.
 */
function haritayiGuncelle(dronelar) {
    dronelar.forEach(drone => {
        const yeniLat = drone.enlem;
        const yeniLng = drone.boylam;
        let aci = 0;

        // Calculate heading if the UAV already exists on the map
        if (ucakIkonlari[drone.id]) {
            const eskiLat = ucakIkonlari[drone.id].getLatLng().lat;
            const eskiLng = ucakIkonlari[drone.id].getLatLng().lng;
            aci = Math.atan2(yeniLat - eskiLat, yeniLng - eskiLng) * (180 / Math.PI) + 45;
        }

        const aktifIkon = getIhaIkonu(drone.durum, aci);

        if (ucakIkonlari[drone.id]) {
            // Update existing UAV marker
            ucakIkonlari[drone.id].setLatLng([yeniLat, yeniLng]);
            ucakIkonlari[drone.id].setIcon(aktifIkon); 
            ucakIkonlari[drone.id].setPopupContent(`<b>${drone.cagriKodu}</b><br>Durum: ${drone.durum}`);
            
            // Trajectory (Comet Tail) Management
            if (!ucakIzleri[drone.id]) {
                const initialColor = (drone.durum === 'RETURNING') ? SABITLER.RENKLER.TEHLIKE : SABITLER.RENKLER.NORMAL;
                ucakIzleri[drone.id] = L.polyline([], {color: initialColor, weight: 2, opacity: 0.5}).addTo(map);
            }
            
            const izKoordinatlari = ucakIzleri[drone.id].getLatLngs();
            izKoordinatlari.push(new L.LatLng(yeniLat, yeniLng));

            // Shift array to maintain max tail length constraint
            if (izKoordinatlari.length > SABITLER.HARITA.KUYRUK_UZUNLUGU) {
                izKoordinatlari.shift(); 
            }

            const dynamicColor = (drone.durum === 'RETURNING') ? SABITLER.RENKLER.TEHLIKE : SABITLER.RENKLER.NORMAL;
            ucakIzleri[drone.id].setLatLngs(izKoordinatlari);
            ucakIzleri[drone.id].setStyle({color: dynamicColor});
            
        } else {
            // Register new UAV marker
            const marker = L.marker([yeniLat, yeniLng], {icon: aktifIkon}).addTo(map);
            marker.bindPopup(`<b>${drone.cagriKodu}</b><br>Durum: ${drone.durum}`);
            ucakIkonlari[drone.id] = marker;
        }
    });
}

// ============================================================================
// WEBSOCKET (STOMP) INFRASTRUCTURE
// ============================================================================

filoyuGetir(); // Trigger initial state
let stompClient = null;

/**
 * Establishes a persistent, real-time connection with the C2 backend.
 * Includes auto-reconnect logic as a fail-safe mechanism.
 */
function websocketBaglan() {
    const socket = new SockJS(SABITLER.WS.URL);
    stompClient = Stomp.over(socket);
    stompClient.debug = null; // Suppress verbose STOMP console logs

    stompClient.connect({}, function (frame) {
        console.log('[WEBSOCKET] Uplink Established: ' + frame);
        
        const statusDiv = document.querySelector('.status');
        if (statusDiv) {
            statusDiv.innerText = 'SISTEM: CEVRIMICI (CANLI AKIS)';
            statusDiv.style.color = SABITLER.RENKLER.NORMAL;
        }

        // Listen for broadcasting coordinates from the backend simulation engine
        stompClient.subscribe(SABITLER.WS.TOPIC, function (mesaj) {
            const dronelar = JSON.parse(mesaj.body);
            ekranaCiz(dronelar);
            haritayiGuncelle(dronelar);
        });
    }, function (error) {
        console.error('[WEBSOCKET] Uplink Lost!', error);
        
        const statusDiv = document.querySelector('.status');
        if (statusDiv) {
            statusDiv.innerText = 'SISTEM: BAGLANTI KOPTU!';
            statusDiv.style.color = SABITLER.RENKLER.TEHLIKE;
        }
        
        // Auto-reconnect interval
        setTimeout(websocketBaglan, 5000);
    });
}

websocketBaglan();

// ============================================================================
// OPERATOR INPUT HANDLING
// ============================================================================

/**
 * Processes the launch of a new UAV from the UI.
 * Applies Guard Clauses to prevent malformed data submissions.
 */
async function yeniIhaFirlat() {
    const cagriKodu = document.getElementById('yeni-cagri-kodu').value;
    const model = document.getElementById('yeni-model').value;
    const hedefSecimi = document.getElementById('yeni-hedef').value.split(',');

    // Guard Clause: Prevent empty call signs
    if (!cagriKodu) {
        alert("SISTEM UYARISI: Lutfen gecerli bir cagri kodu girin!");
        return;
    }

    const yeniDrone = {
        cagriKodu: cagriKodu,
        model: model,
        bataryaSeviyesi: 100.0,
        enlem: SABITLER.US_KOORDINAT.ENLEM,    
        boylam: SABITLER.US_KOORDINAT.BOYLAM,
        hedefEnlem: parseFloat(hedefSecimi[0]), 
        hedefBoylam: parseFloat(hedefSecimi[1]), 
        irtifa: 18000.0,
        hiz: 120.5
    };

    try {
        const response = await fetch(SABITLER.API.EKLE, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(yeniDrone)
        });

        if (response.ok) {
            console.log(`[DEPLOYMENT] ${cagriKodu} successfully registered to the network.`);
            document.getElementById('yeni-cagri-kodu').value = '';
            filoyuGetir(); 
        } else {
            alert("Sistem Hatasi: IHA eklenemedi. Cagri kodunun benzersiz (Unique) oldugundan emin olun.");
        }
    } catch (error) {
        console.error("[DEPLOYMENT ERROR] Connection failure:", error);
    }
}