const API_URL = 'http://localhost:8080/api/drones/filo';

// 1. HARİTAYI BAŞLATMA (Türkiye Koordinatları)
const map = L.map('map').setView([39.0, 35.0], 6);

// Askeri siyah temalı harita altlığı
L.tileLayer('https://{s}.basemaps.cartocdn.com/dark_all/{z}/{x}/{y}{r}.png', {
    attribution: '&copy; C2 Taktik Sistem',
    subdomains: 'abcd',
    maxZoom: 19
}).addTo(map);

// ==========================================
// YENİ EKLENEN KISIM: TAKTİK SINIRLAR
// ==========================================
async function taktikSinirlariCiz() {
    try {
        // OpenStreetMap API'sinden Türkiye'nin GeoJSON (Poligon) sınır verisini çekiyoruz
        const response = await fetch('https://nominatim.openstreetmap.org/search?country=Turkey&polygon_geojson=1&format=json');
        const data = await response.json();
        
        // Gelen verinin içindeki koordinat ağını (geojson) alıyoruz
        const turkiyeGeoJSON = data[0].geojson;

        // Leaflet motoruna bu sınırları askeri bir tasarımla çizmesini söylüyoruz
        L.geoJSON(turkiyeGeoJSON, {
            style: {
                color: '#00ff41',       // Sınır çizgisi rengi (Neon Yeşil)
                weight: 2,              // Çizgi kalınlığı
                opacity: 0.6,           // Çizgi parlaklığı
                fillColor: '#00ff41',   // Ülke içinin rengi
                fillOpacity: 0.05       // İç dolgu saydamlığı (Sadece çok hafif bir yeşil radar aydınlatması)
            }
        }).addTo(map);

    } catch (error) {
        console.error("Taktik sınır verisi çekilemedi:", error);
    }
}

// Sistemi açtığımızda sınırları otomatik çizdiriyoruz
taktikSinirlariCiz();
// ==========================================

// Haritadaki İHA ikonlarını ve izlerini hafızada tutacağımız yer
let ucakIkonlari = {};
let ucakIzleri = {}; // YENİ: İHA izleri (rotaları) için eklendi

// YENİ: Uçak ikonumuzu SVG olarak ve açıya göre dönen yapıda tanımlıyoruz
const getIhaIkonu = (durum, aci) => {
    const renk = (durum === 'RETURNING') ? '#ff003c' : '#00ff41';
    return L.divIcon({
        className: 'custom-div-icon',
        html: `<div style="transform: rotate(${aci}deg); color: ${renk}; font-size: 20px;">✈</div>`,
        iconSize: [20, 20],
        iconAnchor: [10, 10]
    });
};

// 2. BACKEND'DEN VERİ ÇEKME
async function filoyuGetir() {
    try {
        const response = await fetch(API_URL);
        const dronelar = await response.json();
        
        ekranaCiz(dronelar);
        haritayiGuncelle(dronelar);
    } catch (error) {
        console.error("Sistem bağlantı hatası:", error);
    }
}

// 3. SOL MENÜYÜ GÜNCELLEME
function ekranaCiz(dronelar) {
    const listeDiv = document.getElementById('drone-list');
    listeDiv.innerHTML = ''; 

    dronelar.forEach(drone => {
        let durumSinifi = '';
        let renk = '#00ff41'; 
        
        if (drone.durum === 'RETURNING') {
            durumSinifi = 'alert-danger'; 
            renk = '#ff003c';
        } else if (drone.durum === 'MAINTENANCE') {
            renk = 'gray';
        }

        // Eğer uçak zaten dönüyorsa, bakımdaysa veya üsteyse butonu gizle. Sadece görevdeyken (IN_MISSION) butona basılabilsin.
        let butonGorunurluk = (drone.durum === 'IN_MISSION') ? '' : 'd-none';

        const kart = document.createElement('div');
        kart.className = `drone-card ${durumSinifi}`; 
        kart.innerHTML = `
            <div><strong>KOD    :</strong> ${drone.cagriKodu}</div>
            <div><strong>MODEL  :</strong> ${drone.model}</div>
            <div><strong>ŞARJ   :</strong> %${drone.bataryaSeviyesi.toFixed(1)}</div>
            <div><strong>İRTİFA :</strong> ${drone.irtifa} ft</div>
            <div><strong>HIZ    :</strong> ${drone.hiz} knt</div>
            <div><strong>DURUM  :</strong> <span style="color: ${renk}">${drone.durum}</span></div>
            <button class="btn-rtb ${butonGorunurluk}" onclick="manuelRtbEmriVer(${drone.id})">[ DİREKTİF: ÜSSE DÖN ]</button>
        `;
        listeDiv.appendChild(kart);
    });
}

// Arka uca komutu ateşleyen tetiğimiz
async function manuelRtbEmriVer(droneId) {
    try {
        // Arka uçtaki API kapısına POST isteği atıyoruz
        await fetch(`http://localhost:8080/api/drones/${droneId}/rtb`, {
            method: 'POST'
        });
        console.log("Operatör Emri İletildi: Drone ID " + droneId + " üsse çağrıldı.");
        
        // Emri verdikten sonra arayüzün anında tepki vermesi için filoyu beklemeden bir kez çek
        filoyuGetir(); 
    } catch (error) {
        console.error("Komuta iletilemedi!", error);
    }
}

// 4. HARİTAYI GÜNCELLEME (GÜNCELLENEN KISIM - KUYRUK EFEKTİ)
function haritayiGuncelle(dronelar) {
    dronelar.forEach(drone => {
        const yeniLat = drone.enlem;
        const yeniLng = drone.boylam;

        let aci = 0;
        if (ucakIkonlari[drone.id]) {
            const eskiLat = ucakIkonlari[drone.id].getLatLng().lat;
            const eskiLng = ucakIkonlari[drone.id].getLatLng().lng;
            aci = Math.atan2(yeniLat - eskiLat, yeniLng - eskiLng) * (180 / Math.PI) + 45;
        }

        const aktifIkon = getIhaIkonu(drone.durum, aci);

        if (ucakIkonlari[drone.id]) {
            ucakIkonlari[drone.id].setLatLng([yeniLat, yeniLng]);
            ucakIkonlari[drone.id].setIcon(aktifIkon); 
            ucakIkonlari[drone.id].setPopupContent(`<b>${drone.cagriKodu}</b><br>Durum: ${drone.durum}`);
            
            // ==========================================
            // YENİ: KUYRUK (COMET TAIL) EFEKTİ
            // ==========================================
            if (!ucakIzleri[drone.id]) {
                ucakIzleri[drone.id] = L.polyline([], {color: (drone.durum === 'RETURNING' ? '#ff003c' : '#00ff41'), weight: 2, opacity: 0.5}).addTo(map);
            }
            
            // Mevcut koordinatları al
            const izKoordinatlari = ucakIzleri[drone.id].getLatLngs();
            izKoordinatlari.push(new L.LatLng(yeniLat, yeniLng));

            // EĞER kuyruk 0 noktadan uzun olursa, en eskisini sil (Sadece arkasından gelir)
            if (izKoordinatlari.length > 0) {
                izKoordinatlari.shift(); 
            }

            // Çizgiyi güncelle ve duruma göre anında renk değiştir
            ucakIzleri[drone.id].setLatLngs(izKoordinatlari);
            ucakIzleri[drone.id].setStyle({color: (drone.durum === 'RETURNING' ? '#ff003c' : '#00ff41')});
            
        } else {
            const marker = L.marker([yeniLat, yeniLng], {icon: aktifIkon}).addTo(map);
            marker.bindPopup(`<b>${drone.cagriKodu}</b><br>Durum: ${drone.durum}`);
            ucakIkonlari[drone.id] = marker;
        }
    });
}


// ==========================================
// YENİ EKLENEN: WEBSOCKET CANLI DİNLEME (STOMP)
// ==========================================

// İlk açılışta ekran boş kalmasın diye veriyi bir kez API'den çek
filoyuGetir();

let stompClient = null;

function websocketBaglan() {
    // Arka uçta açtığımız kapıya (endpoint) gidiyoruz
    const socket = new SockJS('http://localhost:8080/ws-radar');
    stompClient = Stomp.over(socket);

    // Konsoldaki STOMP log kalabalığını kapatmak için
    stompClient.debug = null; 

    stompClient.connect({}, function (frame) {
        console.log('Telsiz Bağlantısı Kuruldu: ' + frame);
        
        // UI'da ufak bir profesyonel dokunuş (Sol üstteki statü güncellensin)
        const statusDiv = document.querySelector('.status');
        if (statusDiv) {
            statusDiv.innerText = 'SİSTEM: ÇEVRİMİÇİ (CANLI AKIŞ)';
            statusDiv.style.color = '#00ff41'; // Neon yeşil
        }

        // Karargahın yayın yaptığı frekansa abone ol (Subscribe)
        stompClient.subscribe('/topic/drones', function (mesaj) {
            // Sunucudan canlı veri fırlatıldığı anda bu blok çalışır
            const dronelar = JSON.parse(mesaj.body);
            
            // Gelen canlı veriyi eski fonksiyonlarımıza veriyoruz.
            ekranaCiz(dronelar);
            haritayiGuncelle(dronelar);
        });
    }, function (error) {
        console.error('Telsiz bağlantısı koptu!', error);
        
        // UI statü uyarısı
        const statusDiv = document.querySelector('.status');
        if (statusDiv) {
            statusDiv.innerText = 'SİSTEM: BAĞLANTI KOPTU!';
            statusDiv.style.color = '#ff003c';
        }
        
        // Bağlantı koparsa 5 saniye sonra tekrar bağlanmayı dene (Auto-Reconnect)
        setTimeout(websocketBaglan, 5000);
    });
}

// Telsizi çalıştır
websocketBaglan();
// ==========================================


// 5. YENİ İHA FIRLATMA (Operatör Paneli)
async function yeniIhaFirlat() {
    // Ekrana girilen verileri yakala
    const cagriKodu = document.getElementById('yeni-cagri-kodu').value;
    const model = document.getElementById('yeni-model').value;
    const hedefSecimi = document.getElementById('yeni-hedef').value.split(','); // Virgülü baz alarak enlem ve boylamı ayır

    // Güvenlik kontrolü: Boş kayıt atılamaz
    if (!cagriKodu) {
        alert("SİSTEM UYARISI: Lütfen bir çağrı kodu girin!");
        return;
    }

    // Arka ucun beklediği Entity formatında yeni bir JSON nesnesi oluşturuyoruz.
    const yeniDrone = {
        cagriKodu: cagriKodu,
        model: model,
        bataryaSeviyesi: 100.0,
        enlem: 39.92077,    // Her zaman Ankara merkezden fırlatılır
        boylam: 32.85411,
        hedefEnlem: parseFloat(hedefSecimi[0]), // Seçilen şehrin enlemi
        hedefBoylam: parseFloat(hedefSecimi[1]), // Seçilen şehrin boylamı
        irtifa: 18000.0,
        hiz: 120.5
    };

    try {
        const response = await fetch('http://localhost:8080/api/drones/ekle', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(yeniDrone)
        });

        if (response.ok) {
            console.log(cagriKodu + " BAŞARIYLA SİSTEME EKLENDİ.");
            document.getElementById('yeni-cagri-kodu').value = '';
            filoyuGetir(); 
        } else {
            alert("Sistem Hatası: İHA eklenemedi. Çağrı kodunun benzersiz (Unique) olduğundan emin olun.");
        }

    } catch (error) {
        console.error("Bağlantı hatası:", error);
    }
}