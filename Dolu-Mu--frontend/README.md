# Dolu Mu? - Front-End Projesi

Bu proje, mekan doluluk oranlarını gösteren mobil uyumlu bir web arayüzüdür. 

## 🚀 Proje Yapısı
- **index.html**: Splash (Giriş) ekranı.
- **ana-sayfa.html**: Mekanların ve doluluk oranlarının listelendiği ana dashboard.
- **ayarlar.html**: Dil seçimi, KVKK metni ve Karanlık Mod ayarlarının bulunduğu sayfa.
- **style.css**: Tüm görsel tasarım ve karanlık mod değişkenleri.
- **script.js**: Dil değişimi, modal yönetimi ve tema kontrolleri.

## 🛠 API Entegrasyonu İçin Notlar
API bağlantısını yapacak arkadaşın dikkatine:
1. **Veri Çekme**: Ana sayfadaki doluluk oranlarını dinamik hale getirmek için `script.js` içerisine `fetch` fonksiyonlarını ekleyebilirsin.
2. **Dil Desteği**: Mevcut dil sistemi `translations` objesi üzerinden yürümektedir. Yeni diller eklemek istersen bu objeyi genişletebilirsin.
3. **Karanlık Mod**: Tema değişimi `<html>` etiketine eklenen `data-theme="dark"` özniteliği (attribute) ile kontrol edilir. CSS tarafında değişkenler hazırdır.

## 📱 Tasarım Özellikleri
- Figma tasarımına sadık kalınmıştır.
- Modallar (Dil ve KVKK) "Bottom Sheet" yapısında aşağıdan yukarı açılacak şekilde kodlanmıştır.
- Tamamen responsive (mobil uyumlu) yapıdadır.
