# 🦅 C2 Taktik Komuta Kontrol Sistemi

Bu proje, otonom Insansiz Hava Araclari (IHA) icin gelistirilmis gercek zamanli (real-time) bir komuta kontrol ve radar takip simulasyonudur. Sistem, SOLID prensipleri gozetilerek ve durum tabanli (State/Strategy) mimari kullanilarak tasarlanmistir.

## 🚀 One Cikan Ozellikler
* **Gercek Zamanli Radar (WebSocket):** IHA koordinatlari STOMP protokolu uzerinden milisaniyeler icinde haritaya yansitilir.
* **Otonom Zeka (Strategy Pattern):** IHA'larin durumlari (Gorev, Donus, Bakim) karmasik if-else yiginlari yerine OOP Strategy Deseni ile izole edilmistir.
* **Gorsel Interpolasyon & Kuyruk Efekti:** Ucaklar atlamadan suzulerek ilerler ve arkalarinda son 15 koordinati kapsayan taktiksel bir iz (Polyline) birakir.

## 🛠️ Mimari ve Teknolojiler
* **Backend:** Java 17, Spring Boot, WebSocket (STOMP), H2 Database
* **Frontend:** Vanilla JavaScript, HTML5/CSS3, Leaflet.js