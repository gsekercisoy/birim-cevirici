import React, { useEffect, useState } from 'react';
import { api } from './api.js';

const CAT_LABELS = {
  uzunluk: '📏 Uzunluk',
  agirlik: '⚖️ Ağırlık',
  sicaklik: '🌡️ Sıcaklık',
  hacim: '🧪 Hacim',
  hiz: '🚀 Hız',
  veri: '💾 Veri'
};

export default function App() {
  const [cats, setCats] = useState(null); // {cat: {unit: ad}}
  const [cat, setCat] = useState('uzunluk');
  const [from, setFrom] = useState('km');
  const [to, setTo] = useState('mi');
  const [value, setValue] = useState('42.195');
  const [result, setResult] = useState(null);
  const [allResults, setAllResults] = useState(null);
  const [err, setErr] = useState('');

  useEffect(() => {
    api.get('/categories').then(data => {
      setCats(data);
      setCat('uzunluk');
      setFrom('km');
      setTo('mi');
    }).catch(e => setErr(e.message));
  }, []);

  useEffect(() => {
    if (!cats) return;
    const v = parseFloat(value);
    if (isNaN(v)) { setResult(null); setAllResults(null); return; }

    let alive = true;
    if (from && to && from !== to) {
      api.get(`/convert?unit=${cat}&from=${encodeURIComponent(from)}&to=${encodeURIComponent(to)}&value=${v}`)
        .then(d => { if (alive) { setResult(d.result); setErr(''); } })
        .catch(e => { if (alive) { setErr(e.message); setResult(null); } });
    } else {
      setResult(null);
    }
    api.get(`/convert/all?unit=${cat}&from=${encodeURIComponent(from)}&value=${v}`)
      .then(d => { if (alive) setAllResults(d.results); })
      .catch(() => { if (alive) setAllResults(null); });

    return () => { alive = false; };
  }, [cats, cat, from, to, value]);

  if (err && !cats) return <div className="err big">{err}</div>;
  if (!cats) return <div className="muted big">Yükleniyor…</div>;

  const units = Object.keys(cats[cat] || {});

  const switchCat = (c) => {
    setCat(c);
    const us = Object.keys(cats[c]);
    setFrom(us[0]);
    setTo(us[1]);
  };

  return (
    <div className="app">
      <h1>📐 Birim Çevirici v2</h1>
      <p className="lead">Dönüşüm tabloları ve hesap Java REST API'de — arayüz React.</p>

      <div className="tabs">
        {Object.keys(cats).map(c => (
          <button key={c} className={c === cat ? 'active' : ''} onClick={() => switchCat(c)}>
            {CAT_LABELS[c] || c}
          </button>
        ))}
      </div>

      <div className="panel">
        <div className="row">
          <div>
            <label>Değer</label>
            <input type="number" step="any" value={value} onChange={e => setValue(e.target.value)} />
          </div>
          <div>
            <label>Kaynak birim</label>
            <select value={from} onChange={e => setFrom(e.target.value)}>
              {units.map(u => <option key={u} value={u}>{cats[cat][u]} ({u})</option>)}
            </select>
          </div>
        </div>

        <div className="swap-row">
          <button className="swap" onClick={() => { const f = from; setFrom(to); setTo(f); }}>⇅</button>
        </div>

        <div className="row">
          <div className="res">
            <label>Sonuç</label>
            {result === null ? <div className="result muted">—</div> : (
              <div className="result">
                <b>{Number(result).toLocaleString('tr-TR', { maximumFractionDigits: 8 })}</b>
                <span>{cats[cat][to]} ({to})</span>
              </div>
            )}
          </div>
        </div>

        {err && <div className="err">{err}</div>}

        {allResults && (
          <div className="units">
            {Object.entries(allResults).map(([u, r]) => (
              <div key={u}>
                <b>{Number(r).toLocaleString('tr-TR', { maximumFractionDigits: 4 })}</b>
                {cats[cat][u]} ({u})
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
