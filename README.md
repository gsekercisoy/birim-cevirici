# Birim Çevirici v2

React (Vite) arayüz + Java (Spring Boot 2.7) REST API. Tüm dönüşüm tabloları ve hesap
sunucudadır; arayüz sadece API çıktısını gösterir (stateless, veritabanı yok).

## Özellikler

- 6 kategori: uzunluk, ağırlık, sıcaklık, hacim, hız, veri
- Kategori/birim keşfi API'den: `GET /api/categories`
- Çift yönlü çevrim: `GET /api/convert` (sıcaklıkta Kelvin üzerinden köprü)
- Yazdıkça canlı çevrim + tüm birimlere k dokoncu tablo: `GET /api/convert/all`
- Geçersiz kategori/birim → 400 + açıklayıcı mesaj

## Çalıştırma

```bash
cd backend && mvn spring-boot:run     # API :8080
cd frontend && npm install && npm run dev   # UI :5173 (proxy → 8080)
```

## API

| Metot | Yol | Açıklama |
|---|---|---|
| GET | `/api/categories` | `{kategori: {birim: görünen ad}}` |
| GET | `/api/convert?unit=&from=&to=&value=` | Tek birim dönüşümü |
| GET | `/api/convert/all?unit=&from=&value=` | Tüm birimlere karşılaştırma tablosu |

Doğrulama örnekleri: 42.195 km = 26.2188 mi ✓ · 98.6 °F = 37.0 °C ✓

v1 tek dosyalık HTML sürümü git geçmişinde korunmaktadır.
