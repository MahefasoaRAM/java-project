// Régénère les planches PNG (une par parcours) à partir de maquettes_ihm.html, via Chrome headless.
// Usage (depuis java-project/ihm) : node source/generer_images.js source/maquettes_ihm.html maquettes
const { spawn } = require('child_process');
const fs = require('fs');
const os = require('os');
const path = require('path');

const [, , htmlFile, outDir] = process.argv;
const PORT = 9333;
const MARGIN = 40;

const SHOTS = [
  ['P0_attributs-uml', '#p0'],
  ['P1_parcours-emprunter', '#p1'],
  ['P2_parcours-retour-suivi', '#p2'],
  ['P3_parcours-gestion-livres', '#p3'],
  ['P4_parcours-gestion-comptes', '#p4'],
];

const sleep = (ms) => new Promise((r) => setTimeout(r, ms));

(async () => {
  fs.mkdirSync(outDir, { recursive: true });
  const chrome = spawn('google-chrome', [
    '--headless=new', '--disable-gpu', `--remote-debugging-port=${PORT}`,
    `--user-data-dir=${path.join(os.tmpdir(), 'mediatheque-chrome-profile')}`, 'about:blank',
  ], { stdio: 'ignore' });

  let target;
  for (let i = 0; i < 50 && !target; i++) {
    await sleep(200);
    try {
      const list = await (await fetch(`http://127.0.0.1:${PORT}/json`)).json();
      target = list.find((t) => t.type === 'page');
    } catch { /* Chrome pas encore prêt */ }
  }
  if (!target) throw new Error('Chrome DevTools injoignable');

  const ws = new WebSocket(target.webSocketDebuggerUrl);
  await new Promise((r) => ws.addEventListener('open', r));
  let id = 0;
  const pending = new Map();
  ws.addEventListener('message', (ev) => {
    const msg = JSON.parse(ev.data);
    if (msg.id && pending.has(msg.id)) {
      const { resolve, reject } = pending.get(msg.id);
      pending.delete(msg.id);
      msg.error ? reject(new Error(msg.error.message)) : resolve(msg.result);
    }
  });
  const send = (method, params = {}) => new Promise((resolve, reject) => {
    const msgId = ++id;
    pending.set(msgId, { resolve, reject });
    ws.send(JSON.stringify({ id: msgId, method, params }));
  });
  const evaluate = async (expr) =>
    (await send('Runtime.evaluate', { expression: expr, awaitPromise: true, returnByValue: true })).result.value;

  await send('Page.enable');
  await send('Emulation.setDeviceMetricsOverride', { width: 1600, height: 1000, deviceScaleFactor: 1, mobile: false });
  await send('Page.navigate', { url: 'file://' + path.resolve(htmlFile) + '?zoom=1' });
  for (let i = 0; i < 100; i++) {
    await sleep(200);
    if ((await evaluate('document.body && document.body.dataset.ready')) === 'true') break;
  }
  await sleep(300);

  for (const [name, selector] of SHOTS) {
    const r = await evaluate(`(() => {
      const b = document.querySelector(${JSON.stringify(selector)}).getBoundingClientRect();
      return { x: b.left + scrollX, y: b.top + scrollY, width: b.width, height: b.height };
    })()`);
    const clip = {
      x: Math.max(0, r.x - MARGIN), y: Math.max(0, r.y - MARGIN),
      width: r.width + 2 * MARGIN, height: r.height + 2 * MARGIN, scale: 1,
    };
    const { data } = await send('Page.captureScreenshot', { format: 'png', clip, captureBeyondViewport: true });
    fs.writeFileSync(path.join(outDir, name + '.png'), Buffer.from(data, 'base64'));
    console.log(name, Math.round(clip.width) + 'x' + Math.round(clip.height));
  }

  ws.close();
  chrome.kill();
})().catch((e) => { console.error(e); process.exit(1); });
