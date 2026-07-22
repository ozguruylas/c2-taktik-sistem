# 🦅 C2 Taktik Komuta Kontrol Sistemi

Bu proje, otonom İnsansız Hava Araçları (İHA) için geliştirilmiş gerçek zamanlı (real-time) bir komuta kontrol ve radar takip simülasyonudur. Sistem, SOLID prensipleri gözetilerek ve durum tabanlı (State/Strategy) mimari kullanılarak tasarlanmıştır.

## 🚀 Öne Çıkan Özellikler
* **Gerçek Zamanlı Radar (WebSocket):** İHA koordinatları STOMP protokolü üzerinden milisaniyeler içinde haritaya yansıtılır.
* **Otonom Zeka (Strategy Pattern):** İHA'ların durumları (Görev, Dönüş, Bakım) karmaşık if-else yığınları yerine OOP Strategy Deseni ile izole edilmiştir.
* **Görsel İnterpolasyon & Kuyruk Efekti:** Uçaklar atlamadan süzülerek ilerler ve arkalarında son 15 koordinatı kapsayan taktiksel bir iz (Polyline) bırakır.

## 🛠️ Mimari ve Teknolojiler
* **Backend:** Java 17, Spring Boot, WebSocket (STOMP), H2 Database
* **Frontend:** Vanilla JavaScript, HTML5/CSS3, Leaflet.js