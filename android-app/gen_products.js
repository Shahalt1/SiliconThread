// Deterministic generator for ~200 SiliconThread hardware products.
// Output: android-app/assets/products.json
const fs = require('fs');
const path = require('path');

let seed = 42;
function rand() { seed = (seed * 1664525 + 1013904223) >>> 0; return seed / 0xffffffff; }
function pick(arr) { return arr[Math.floor(rand() * arr.length)]; }
function between(lo, hi) { return lo + rand() * (hi - lo); }
function intBetween(lo, hi) { return Math.floor(between(lo, hi + 1)); }
function round2(n) { return Math.round(n * 100) / 100; }

function slug(s) {
  return s.toLowerCase().replace(/[^a-z0-9]+/g, '-').replace(/^-|-$/g, '');
}

const products = [];

function add(p) {
  p.id = slug(`${p.brand}-${p.model}`);
  p.testId = `product-card-${p.id}`;
  if (!p.image) p.image = `img_${p.category.toLowerCase()}_${(products.length % 8) + 1}`;
  if (!p.rating) p.rating = round2(between(3.6, 5.0));
  if (!p.reviewCount) p.reviewCount = intBetween(12, 4200);
  if (!p.inStock) p.inStock = rand() > 0.08;
  if (!p.deal && rand() > 0.65) {
    const pct = intBetween(5, 25);
    p.deal = { label: `${pct}% OFF`, discountPct: pct };
    p.originalPrice = round2(p.price * (1 + pct / 100));
  }
  if (!p.tags) p.tags = [];
  products.push(p);
}

// ============ GPUs ============
const gpus = [
  ['NVIDIA', 'GeForce RTX 5090', 1999, '32GB GDDR7', 21760, 575, ['flagship','featured','gaming','ai']],
  ['NVIDIA', 'GeForce RTX 5080', 1199, '16GB GDDR7', 10752, 360, ['featured','gaming']],
  ['NVIDIA', 'GeForce RTX 5070 Ti', 799, '16GB GDDR7', 8960, 300, ['featured','gaming']],
  ['NVIDIA', 'GeForce RTX 5070', 549, '12GB GDDR7', 6144, 250, ['gaming','value']],
  ['NVIDIA', 'GeForce RTX 4090', 1599, '24GB GDDR6X', 16384, 450, ['gaming','ai']],
  ['NVIDIA', 'GeForce RTX 4080 Super', 999, '16GB GDDR6X', 10240, 320, ['gaming']],
  ['NVIDIA', 'GeForce RTX 4070 Ti Super', 799, '16GB GDDR6X', 8448, 285, ['gaming']],
  ['NVIDIA', 'GeForce RTX 4070 Super', 599, '12GB GDDR6X', 7168, 220, ['gaming','value']],
  ['NVIDIA', 'GeForce RTX 4060 Ti', 399, '8GB GDDR6', 4352, 160, ['gaming','value']],
  ['NVIDIA', 'GeForce RTX 4060', 299, '8GB GDDR6', 3072, 115, ['gaming','value','deal']],
  ['NVIDIA', 'GeForce RTX 3090 Ti', 1099, '24GB GDDR6X', 10752, 450, ['gaming']],
  ['NVIDIA', 'GeForce RTX 3080 Ti', 699, '12GB GDDR6X', 10240, 350, ['gaming','deal']],
  ['NVIDIA', 'GeForce RTX 3070', 379, '8GB GDDR6', 5888, 220, ['gaming','deal']],
  ['NVIDIA', 'GeForce RTX 3060 Ti', 299, '8GB GDDR6', 4864, 200, ['gaming','value']],
  ['NVIDIA', 'GeForce RTX 3060 12GB', 249, '12GB GDDR6', 3584, 170, ['gaming','value']],
  ['AMD', 'Radeon RX 7900 XTX', 949, '24GB GDDR6', 6144, 355, ['gaming','flagship']],
  ['AMD', 'Radeon RX 7900 XT', 749, '20GB GDDR6', 5376, 315, ['gaming']],
  ['AMD', 'Radeon RX 7900 GRE', 549, '16GB GDDR6', 5120, 260, ['gaming']],
  ['AMD', 'Radeon RX 7800 XT', 499, '16GB GDDR6', 3840, 263, ['gaming','value']],
  ['AMD', 'Radeon RX 7700 XT', 419, '12GB GDDR6', 3456, 245, ['gaming','value']],
  ['AMD', 'Radeon RX 7600', 269, '8GB GDDR6', 2048, 165, ['gaming','value','deal']],
  ['AMD', 'Radeon RX 6950 XT', 599, '16GB GDDR6', 5120, 335, ['gaming','deal']],
  ['AMD', 'Radeon RX 6800 XT', 449, '16GB GDDR6', 4608, 300, ['gaming','deal']],
  ['AMD', 'Radeon RX 6700 XT', 319, '12GB GDDR6', 2560, 230, ['gaming','value']],
  ['Intel', 'Arc B580', 249, '12GB GDDR6', 2944, 190, ['gaming','value','featured']],
  ['Intel', 'Arc B570', 219, '10GB GDDR6', 2560, 150, ['gaming','value']],
  ['Intel', 'Arc A770 16GB', 329, '16GB GDDR6', 4096, 225, ['gaming']],
  ['Intel', 'Arc A750', 249, '8GB GDDR6', 3584, 225, ['gaming','value']],
  ['ASUS', 'ROG Astral RTX 5090 OC', 2399, '32GB GDDR7', 21760, 600, ['flagship','overclocked']],
  ['MSI', 'GeForce RTX 5080 Suprim X', 1349, '16GB GDDR7', 10752, 360, ['featured','overclocked']],
  ['Gigabyte', 'Aorus RTX 4090 Master', 1799, '24GB GDDR6X', 16384, 450, ['overclocked']],
  ['EVGA', 'GeForce RTX 3090 FTW3', 999, '24GB GDDR6X', 10496, 400, ['gaming']],
  ['Sapphire', 'Nitro+ RX 7900 XTX', 1049, '24GB GDDR6', 6144, 355, ['overclocked']],
  ['PowerColor', 'Red Devil RX 7800 XT', 549, '16GB GDDR6', 3840, 263, ['gaming']],
  ['XFX', 'Speedster MERC 310 RX 7900 XT', 799, '20GB GDDR6', 5376, 315, ['gaming']],
];

gpus.forEach(([brand, model, price, mem, cores, tdp, tags]) => {
  add({
    category: 'GPU',
    brand, model, price: round2(price),
    short: `${mem} • ${cores} cores • ${tdp}W`,
    description: `${brand} ${model} delivers next-gen gaming and AI compute with ${mem} of high-bandwidth memory and ${cores} processing cores. Ray tracing, AI upscaling, and 4K/8K gameplay ready.`,
    specs: {
      'Memory': mem,
      'Cores': String(cores),
      'TDP': `${tdp}W`,
      'Interface': 'PCIe 5.0 x16',
      'Ports': '3x DisplayPort 2.1, 1x HDMI 2.1',
      'Power Connector': tdp > 350 ? '16-pin 12V-2x6' : '2x 8-pin'
    },
    benchmark: {
      '4K Gaming (FPS)': intBetween(60, 240),
      'AI TOPS': intBetween(200, 4500),
      '3DMark Time Spy': intBetween(8000, 36000)
    },
    tags
  });
});

// ============ CPUs ============
const cpus = [
  ['Intel', 'Core i9-14900K', 589, 24, 32, 6.0, 125, 'LGA1700', ['flagship','featured','gaming','overclocked']],
  ['Intel', 'Core i9-14900KF', 559, 24, 32, 6.0, 125, 'LGA1700', ['gaming']],
  ['Intel', 'Core i7-14700K', 419, 20, 28, 5.6, 125, 'LGA1700', ['gaming','featured']],
  ['Intel', 'Core i7-14700KF', 389, 20, 28, 5.6, 125, 'LGA1700', ['gaming']],
  ['Intel', 'Core i5-14600K', 319, 14, 20, 5.3, 125, 'LGA1700', ['gaming','value']],
  ['Intel', 'Core i5-14600KF', 289, 14, 20, 5.3, 125, 'LGA1700', ['gaming','value','deal']],
  ['Intel', 'Core i9-13900K', 499, 24, 32, 5.8, 125, 'LGA1700', ['gaming','deal']],
  ['Intel', 'Core i7-13700K', 379, 16, 24, 5.4, 125, 'LGA1700', ['gaming']],
  ['Intel', 'Core i5-13600K', 269, 14, 20, 5.1, 125, 'LGA1700', ['gaming','value']],
  ['Intel', 'Core Ultra 9 285K', 619, 24, 24, 5.7, 125, 'LGA1851', ['flagship','featured']],
  ['Intel', 'Core Ultra 7 265K', 419, 20, 20, 5.5, 125, 'LGA1851', ['featured']],
  ['Intel', 'Core Ultra 5 245K', 309, 14, 14, 5.2, 125, 'LGA1851', ['value']],
  ['Intel', 'Xeon w9-3495X', 5889, 56, 112, 4.8, 350, 'LGA4677', ['workstation','ai']],
  ['Intel', 'Xeon w7-3465X', 2989, 28, 56, 4.8, 300, 'LGA4677', ['workstation']],
  ['AMD', 'Ryzen 9 9950X', 649, 16, 32, 5.7, 170, 'AM5', ['flagship','featured','gaming']],
  ['AMD', 'Ryzen 9 9900X', 499, 12, 24, 5.6, 120, 'AM5', ['gaming']],
  ['AMD', 'Ryzen 7 9700X', 359, 8, 16, 5.5, 65, 'AM5', ['gaming','value']],
  ['AMD', 'Ryzen 5 9600X', 279, 6, 12, 5.4, 65, 'AM5', ['gaming','value']],
  ['AMD', 'Ryzen 9 7950X3D', 699, 16, 32, 5.7, 120, 'AM5', ['gaming','featured']],
  ['AMD', 'Ryzen 9 7900X3D', 599, 12, 24, 5.6, 120, 'AM5', ['gaming']],
  ['AMD', 'Ryzen 7 7800X3D', 449, 8, 16, 5.0, 120, 'AM5', ['gaming','featured','deal']],
  ['AMD', 'Ryzen 9 7950X', 549, 16, 32, 5.7, 170, 'AM5', ['gaming','deal']],
  ['AMD', 'Ryzen 9 7900X', 429, 12, 24, 5.6, 170, 'AM5', ['gaming']],
  ['AMD', 'Ryzen 7 7700X', 329, 8, 16, 5.4, 105, 'AM5', ['gaming','value']],
  ['AMD', 'Ryzen 5 7600X', 249, 6, 12, 5.3, 105, 'AM5', ['gaming','value']],
  ['AMD', 'Ryzen 7 5800X3D', 329, 8, 16, 4.5, 105, 'AM4', ['gaming','deal']],
  ['AMD', 'Ryzen 9 5950X', 419, 16, 32, 4.9, 105, 'AM4', ['workstation']],
  ['AMD', 'Ryzen 5 5600X', 159, 6, 12, 4.6, 65, 'AM4', ['gaming','value','deal']],
  ['AMD', 'Threadripper Pro 7995WX', 9999, 96, 192, 5.1, 350, 'sTR5', ['workstation','ai']],
  ['AMD', 'Threadripper 7980X', 4999, 64, 128, 5.1, 350, 'sTR5', ['workstation']],
];

cpus.forEach(([brand, model, price, cores, threads, clock, tdp, sock, tags]) => {
  add({
    category: 'CPU',
    brand, model, price: round2(price),
    short: `${cores}C/${threads}T • up to ${clock} GHz`,
    description: `${brand} ${model} is a high-performance processor with ${cores} cores and ${threads} threads, designed for gaming, content creation, and demanding workstation tasks.`,
    specs: {
      'Cores / Threads': `${cores} / ${threads}`,
      'Max Boost Clock': `${clock} GHz`,
      'TDP': `${tdp}W`,
      'Socket': sock,
      'Cache (L3)': `${intBetween(16,128)} MB`,
      'Process': pick(['Intel 7','Intel 4','TSMC 5nm','TSMC 4nm','TSMC 3nm'])
    },
    benchmark: {
      'Cinebench R23 Multi': intBetween(15000, 95000),
      'Geekbench 6 Single': intBetween(2200, 3500),
      'Gaming 1080p (avg FPS)': intBetween(140, 320)
    },
    tags
  });
});

// ============ TPUs ============
const tpus = [
  ['Google', 'Coral USB Accelerator', 79, '4 TOPS', '2W', ['featured','ai']],
  ['Google', 'Coral Dev Board Mini', 129, '4 TOPS', '5W', ['ai']],
  ['Google', 'Coral M.2 Accelerator A+E', 39, '4 TOPS', '2W', ['ai','value']],
  ['Google', 'Coral PCIe Accelerator', 89, '4 TOPS', '3W', ['ai']],
  ['Hailo', 'Hailo-8 M.2 AI Accelerator', 199, '26 TOPS', '2.5W', ['ai','featured']],
  ['Hailo', 'Hailo-10 Generative AI Accelerator', 299, '40 TOPS', '3.5W', ['ai','featured']],
  ['Memryx', 'MX3 USB Module', 149, '5 TOPS', '1.5W', ['ai']],
  ['Kneron', 'KL720 AI USB Dongle', 89, '1.5 TOPS', '1.2W', ['ai','value']],
  ['Sipeed', 'MaixCAM TPU Edge Kit', 69, '1 TOPS', '1.2W', ['ai','value']],
  ['Rockchip', 'RK3588 NPU Module', 99, '6 TOPS', '5W', ['ai']],
];

tpus.forEach(([brand, model, price, tops, power, tags]) => {
  add({
    category: 'TPU',
    brand, model, price: round2(price),
    short: `${tops} • ${power}`,
    description: `${brand} ${model} is a purpose-built edge AI accelerator delivering ${tops} of inference performance within a tiny ${power} envelope. Plug-and-play for on-device computer vision, audio, and LLM tasks.`,
    specs: {
      'Performance': tops,
      'Power': power,
      'Interface': pick(['USB 3.0','M.2 2230','M.2 2280','PCIe x1','PCIe x4']),
      'Framework Support': 'TensorFlow Lite, ONNX, PyTorch',
      'OS Support': 'Linux, Windows, Android'
    },
    benchmark: {
      'MobileNet v2 inferences/sec': intBetween(150, 1800),
      'YOLOv5 FPS': intBetween(20, 240)
    },
    tags
  });
});

// ============ AI Accelerators ============
const ais = [
  ['NVIDIA', 'H200 NVL', 29999, '141GB HBM3e', '700W', ['flagship','featured','ai']],
  ['NVIDIA', 'H100 SXM5', 24999, '80GB HBM3', '700W', ['flagship','ai']],
  ['NVIDIA', 'H100 PCIe', 22999, '80GB HBM2e', '350W', ['ai','featured']],
  ['NVIDIA', 'A100 80GB', 16999, '80GB HBM2e', '400W', ['ai']],
  ['NVIDIA', 'L40S', 9999, '48GB GDDR6', '350W', ['ai','featured']],
  ['NVIDIA', 'L4 Tensor', 2599, '24GB GDDR6', '72W', ['ai','value']],
  ['NVIDIA', 'GH200 Grace Hopper Superchip', 39999, '480GB LPDDR5X', '1000W', ['flagship','ai']],
  ['AMD', 'Instinct MI300X', 19999, '192GB HBM3', '750W', ['ai','featured']],
  ['AMD', 'Instinct MI250X', 11999, '128GB HBM2e', '560W', ['ai']],
  ['AMD', 'Instinct MI210', 8999, '64GB HBM2e', '300W', ['ai']],
  ['Intel', 'Gaudi 3', 15999, '128GB HBM2e', '900W', ['ai','featured']],
  ['Intel', 'Gaudi 2', 7999, '96GB HBM2e', '600W', ['ai','deal']],
  ['Tenstorrent', 'Wormhole n300s', 1399, '24GB GDDR6', '300W', ['ai','featured']],
  ['Tenstorrent', 'Blackhole p100a', 999, '32GB GDDR6', '300W', ['ai']],
  ['Groq', 'LPU Inference Card', 24999, '230MB SRAM', '300W', ['ai']],
];

ais.forEach(([brand, model, price, mem, power, tags]) => {
  add({
    category: 'AI Accelerator',
    brand, model, price: round2(price),
    short: `${mem} • ${power}`,
    description: `${brand} ${model} is a data-center grade AI accelerator built for large-scale training and inference. Pairs ${mem} of high-bandwidth memory with a ${power} compute envelope to crunch transformer workloads at scale.`,
    specs: {
      'Memory': mem,
      'TDP': power,
      'Form Factor': pick(['PCIe Gen5 x16','OAM','SXM5','OAM (UBB-compatible)']),
      'FP16 (TFLOPS)': String(intBetween(180, 4000)),
      'FP8 / INT8 (TOPS)': String(intBetween(400, 9000)),
      'Interconnect': pick(['NVLink 4','Infinity Fabric','PCIe Gen5','CXL 2.0'])
    },
    benchmark: {
      'Llama-2 70B tokens/sec': intBetween(40, 2400),
      'BERT-Large train (samples/s)': intBetween(800, 22000)
    },
    tags
  });
});

// ============ RAM ============
const ramVendors = ['Corsair', 'G.Skill', 'Kingston', 'Crucial', 'TeamGroup', 'Patriot'];
const ramLines = {
  'Corsair': ['Vengeance', 'Dominator Titanium', 'Dominator Platinum'],
  'G.Skill': ['Trident Z5 Neo', 'Trident Z5 RGB', 'Ripjaws S5', 'Flare X5'],
  'Kingston': ['Fury Beast', 'Fury Renegade', 'Fury Renegade RGB'],
  'Crucial': ['Pro', 'Pro Overclocking', 'Ballistix MAX'],
  'TeamGroup': ['T-Force Delta RGB', 'T-Create Expert', 'Vulcan Z'],
  'Patriot': ['Viper Venom', 'Viper Xtreme 5', 'Viper Elite 5']
};
const ramConfigs = [
  // [type, capacity, speed]
  ['DDR5', 16, 6000], ['DDR5', 32, 6000], ['DDR5', 32, 6400], ['DDR5', 32, 7200],
  ['DDR5', 32, 7600], ['DDR5', 32, 8000], ['DDR5', 64, 6000], ['DDR5', 64, 6400],
  ['DDR5', 64, 7200], ['DDR5', 96, 6400], ['DDR5', 128, 5600],
  ['DDR4', 16, 3200], ['DDR4', 16, 3600], ['DDR4', 32, 3200], ['DDR4', 32, 3600],
  ['DDR4', 32, 4000], ['DDR4', 64, 3200], ['DDR4', 64, 3600]
];
let ramCount = 0;
while (ramCount < 25) {
  const vendor = pick(ramVendors);
  const line = pick(ramLines[vendor]);
  const [type, cap, speed] = pick(ramConfigs);
  const cl = type === 'DDR5' ? intBetween(28, 40) : intBetween(14, 22);
  const price = round2(cap * (type === 'DDR5' ? 3.2 : 2.1) + (speed - 3200) * 0.05);
  const model = `${line} ${cap}GB (2x${cap/2}GB) ${type}-${speed} CL${cl}`;
  add({
    category: 'RAM',
    brand: vendor, model, price,
    short: `${cap}GB ${type}-${speed} CL${cl}`,
    description: `${vendor} ${line} memory kit. ${cap}GB total in a 2x${cap/2}GB dual-channel configuration, running at ${speed} MT/s with CAS latency ${cl}. XMP/EXPO ready.`,
    specs: {
      'Type': type,
      'Capacity': `${cap} GB (2x${cap/2}GB)`,
      'Speed': `${speed} MT/s`,
      'CAS Latency': `CL${cl}`,
      'Voltage': type === 'DDR5' ? '1.35V' : '1.35V',
      'Profile': type === 'DDR5' ? 'XMP 3.0 / EXPO' : 'XMP 2.0',
      'Heatspreader': pick(['Aluminum','Aluminum w/ RGB','Brushed aluminum'])
    },
    benchmark: {
      'AIDA64 Read (GB/s)': intBetween(40, 110),
      'AIDA64 Write (GB/s)': intBetween(38, 105),
      'Latency (ns)': intBetween(50, 80)
    },
    tags: ['gaming', cap >= 64 ? 'workstation' : 'value']
  });
  ramCount++;
}

// ============ SSDs ============
const ssdVendors = ['Samsung', 'WD', 'Seagate', 'Crucial', 'Kingston', 'SK Hynix', 'Sabrent', 'Corsair'];
const ssdLines = {
  'Samsung': ['990 Pro', '990 Evo Plus', '980 Pro', '870 Evo'],
  'WD': ['Black SN850X', 'Black SN8100', 'Blue SN580', 'Red SN700'],
  'Seagate': ['FireCuda 540', 'FireCuda 530', 'IronWolf 525'],
  'Crucial': ['T705', 'T700', 'T500', 'P5 Plus'],
  'Kingston': ['Fury Renegade', 'KC3000', 'NV2'],
  'SK Hynix': ['Platinum P41', 'Platinum P51', 'Gold P31'],
  'Sabrent': ['Rocket 5', 'Rocket 4 Plus', 'Rocket Q4'],
  'Corsair': ['MP700 Pro', 'MP600 Pro NH', 'MP600 GS']
};
const ssdCaps = [500, 1000, 2000, 4000, 8000];
let ssdCount = 0;
while (ssdCount < 25) {
  const vendor = pick(ssdVendors);
  const line = pick(ssdLines[vendor]);
  const cap = pick(ssdCaps);
  const gen = pick([4, 4, 4, 5]);
  const seq = gen === 5 ? intBetween(11000, 14500) : intBetween(5000, 7500);
  const price = round2(cap / 1000 * (gen === 5 ? 110 : 78));
  const model = `${line} ${cap >= 1000 ? cap/1000 + 'TB' : cap + 'GB'}`;
  add({
    category: 'SSD',
    brand: vendor, model, price,
    short: `${cap >= 1000 ? cap/1000 + 'TB' : cap + 'GB'} • PCIe ${gen}.0 • up to ${seq} MB/s`,
    description: `${vendor} ${line} NVMe SSD delivers up to ${seq} MB/s sequential read on PCIe ${gen}.0 x4. Ideal for gaming, content creation, and AI training datasets.`,
    specs: {
      'Capacity': cap >= 1000 ? `${cap/1000} TB` : `${cap} GB`,
      'Interface': `PCIe Gen${gen} x4, NVMe 2.0`,
      'Form Factor': 'M.2 2280',
      'Sequential Read': `${seq} MB/s`,
      'Sequential Write': `${seq - intBetween(500, 2000)} MB/s`,
      'Endurance (TBW)': `${cap * intBetween(600, 1200)}`,
      'Warranty': '5 years'
    },
    benchmark: {
      '4K Random Read IOPS': intBetween(800000, 1800000),
      '4K Random Write IOPS': intBetween(900000, 1700000)
    },
    tags: ['gaming', gen === 5 ? 'flagship' : 'value']
  });
  ssdCount++;
}

// ============ Motherboards ============
const moboVendors = ['ASUS', 'MSI', 'Gigabyte', 'ASRock', 'NZXT', 'Biostar'];
const moboChipsets = ['Z890', 'Z790', 'B860', 'B760', 'H770', 'X870E', 'X870', 'X670E', 'B850', 'B650E', 'B650'];
const moboLines = {
  'ASUS': ['ROG Maximus', 'ROG Strix', 'TUF Gaming', 'Prime'],
  'MSI': ['MEG Godlike', 'MPG Carbon', 'MAG Tomahawk', 'PRO'],
  'Gigabyte': ['Aorus Master', 'Aorus Elite', 'Gaming X', 'UD'],
  'ASRock': ['Taichi', 'Steel Legend', 'Phantom Gaming', 'Pro RS'],
  'NZXT': ['N7', 'N5'],
  'Biostar': ['Racing', 'Valkyrie']
};
let moboCount = 0;
while (moboCount < 25) {
  const vendor = pick(moboVendors);
  const line = pick(moboLines[vendor]);
  const chip = pick(moboChipsets);
  const tier = chip.includes('X') || chip.includes('Z') ? 'high-end' : 'mid-range';
  const price = round2(tier === 'high-end' ? between(350, 999) : between(160, 320));
  const formFactor = pick(['ATX','ATX','ATX','E-ATX','Micro-ATX','Mini-ITX']);
  const socket = chip.startsWith('Z') || chip.startsWith('B7') || chip.startsWith('H') || chip === 'B860' ? (chip === 'Z890' || chip === 'B860' ? 'LGA1851' : 'LGA1700') : (chip.startsWith('X870') || chip === 'B850' ? 'AM5' : 'AM5');
  const memType = (chip === 'Z890' || chip === 'B860' || chip.startsWith('X870') || chip === 'B850' || chip === 'X670E' || chip === 'B650E' || chip === 'B650' || chip === 'Z790' || chip === 'B760' || chip === 'H770') ? 'DDR5' : 'DDR5';
  const model = `${line} ${chip} ${formFactor}`;
  add({
    category: 'Motherboard',
    brand: vendor, model, price,
    short: `${chip} • ${socket} • ${formFactor}`,
    description: `${vendor} ${line} ${chip} motherboard with ${socket} socket, ${memType} support, and ${formFactor} form factor. Loaded with Wi-Fi 7, USB4, and PCIe 5.0.`,
    specs: {
      'Chipset': chip,
      'Socket': socket,
      'Form Factor': formFactor,
      'Memory': `4x ${memType} DIMM (up to 256GB)`,
      'PCIe': '1x PCIe 5.0 x16, 1x PCIe 4.0 x16',
      'M.2 Slots': String(intBetween(3, 5)),
      'Networking': 'Wi-Fi 7, 2.5GbE (or 10GbE on high-end)',
      'USB': 'USB4 + 10x USB 3.x'
    },
    benchmark: {
      'VRM Phases': intBetween(12, 24),
      'Power Stages (A)': intBetween(60, 110)
    },
    tags: ['gaming', tier === 'high-end' ? 'flagship' : 'value']
  });
  moboCount++;
}

// ============ Peripherals ============
const peripherals = [
  // Keyboards
  ['Razer', 'Huntsman V3 Pro TKL', 'Keyboard', 219, 'Analog optical TKL keyboard with Razer Synapse profiles', { 'Switches':'Razer Analog Optical', 'Layout':'TKL', 'Connectivity':'USB-C', 'Lighting':'Per-key RGB'}],
  ['Logitech', 'G915 X Lightspeed TKL', 'Keyboard', 219, 'Wireless low-profile mechanical TKL', { 'Switches':'GL Low-profile', 'Layout':'TKL', 'Connectivity':'Lightspeed / Bluetooth / USB-C', 'Lighting':'Per-key RGB'}],
  ['SteelSeries', 'Apex Pro Mini Wireless', 'Keyboard', 239, '60% wireless OmniPoint adjustable actuation keyboard', { 'Switches':'OmniPoint 2.0', 'Layout':'60%', 'Connectivity':'2.4GHz / Bluetooth / USB-C', 'Lighting':'Per-key RGB'}],
  ['Keychron', 'Q1 Pro QMK Wireless', 'Keyboard', 199, 'Aluminum 75% wireless mechanical keyboard', { 'Switches':'Hot-swap MX', 'Layout':'75%', 'Connectivity':'Bluetooth / USB-C', 'Lighting':'South-facing RGB'}],
  ['Corsair', 'K100 Air Wireless', 'Keyboard', 279, 'Ultra-thin wireless low-profile gaming keyboard', { 'Switches':'Cherry MX Ultra-Low Profile Tactile', 'Layout':'Full-size', 'Connectivity':'Slipstream / BT / USB-C', 'Lighting':'Per-key RGB'}],
  ['Drop', 'CSTM80 Wireless', 'Keyboard', 199, 'Hot-swap 80% wireless mechanical keyboard', { 'Switches':'Holy Panda X', 'Layout':'80%', 'Connectivity':'Bluetooth / USB-C', 'Lighting':'North-facing RGB'}],
  ['ASUS', 'ROG Strix Scope II 96 Wireless', 'Keyboard', 199, 'ROG NX 96% wireless gaming keyboard', { 'Switches':'ROG NX Snow', 'Layout':'96%', 'Connectivity':'2.4GHz / BT / USB-C', 'Lighting':'Per-key RGB'}],
  ['HyperX', 'Alloy Rise 75', 'Keyboard', 179, 'Compact 75% hot-swappable RGB keyboard', { 'Switches':'HyperX Linear', 'Layout':'75%', 'Connectivity':'USB-C', 'Lighting':'Per-key RGB'}],

  // Mice
  ['Razer', 'Viper V3 Pro', 'Mouse', 159, 'Ultra-light wireless esports mouse', { 'Sensor':'Focus Pro 35K', 'DPI':'35,000', 'Weight':'54g', 'Connectivity':'HyperSpeed / USB-C'}],
  ['Logitech', 'G Pro X Superlight 2', 'Mouse', 159, 'Tournament-grade wireless mouse', { 'Sensor':'HERO 2', 'DPI':'32,000', 'Weight':'60g', 'Connectivity':'Lightspeed / USB-C'}],
  ['SteelSeries', 'Aerox 5 Wireless', 'Mouse', 139, 'Ultra-light honeycomb wireless mouse', { 'Sensor':'TrueMove Air', 'DPI':'18,000', 'Weight':'74g', 'Connectivity':'Quantum 2.0 / BT / USB-C'}],
  ['Glorious', 'Model O Wireless', 'Mouse', 79, 'Lightweight honeycomb wireless mouse', { 'Sensor':'BAMF', 'DPI':'19,000', 'Weight':'69g', 'Connectivity':'2.4GHz / USB-C'}],
  ['Corsair', 'M75 Air Wireless', 'Mouse', 149, 'Ultra-light wireless gaming mouse', { 'Sensor':'Marksman', 'DPI':'26,000', 'Weight':'60g', 'Connectivity':'Slipstream / BT / USB-C'}],
  ['Pulsar', 'X2H Mini', 'Mouse', 99, 'Asymmetric small-hand esports mouse', { 'Sensor':'PAW3395', 'DPI':'26,000', 'Weight':'52g', 'Connectivity':'2.4GHz / USB-C'}],

  // Monitors
  ['ASUS', 'ROG Swift PG32UCDM 4K OLED', 'Monitor', 1299, '32" 4K 240Hz QD-OLED gaming monitor', { 'Panel':'QD-OLED', 'Resolution':'3840x2160', 'Refresh':'240Hz', 'Response':'0.03ms', 'Inputs':'HDMI 2.1 x2, DP 2.1, USB-C'}],
  ['LG', 'UltraGear 27GR95QE OLED', 'Monitor', 899, '27" QHD 240Hz OLED gaming monitor', { 'Panel':'W-OLED', 'Resolution':'2560x1440', 'Refresh':'240Hz', 'Response':'0.03ms', 'Inputs':'HDMI 2.1 x2, DP 1.4'}],
  ['Samsung', 'Odyssey OLED G9 49"', 'Monitor', 1599, '49" 5K2K 240Hz ultrawide OLED', { 'Panel':'QD-OLED', 'Resolution':'5120x1440', 'Refresh':'240Hz', 'Response':'0.03ms', 'Inputs':'HDMI 2.1, DP 1.4, USB-C'}],
  ['Alienware', 'AW3225QF 4K QD-OLED', 'Monitor', 1199, '32" 4K 240Hz curved QD-OLED', { 'Panel':'QD-OLED', 'Resolution':'3840x2160', 'Refresh':'240Hz', 'Response':'0.03ms', 'Inputs':'HDMI 2.1, DP 1.4, USB-C'}],
  ['MSI', 'MPG 491CQP QD-OLED', 'Monitor', 1299, '49" 5K2K 144Hz ultrawide QD-OLED', { 'Panel':'QD-OLED', 'Resolution':'5120x1440', 'Refresh':'144Hz', 'Response':'0.03ms', 'Inputs':'HDMI 2.1 x2, DP 1.4, USB-C'}],
  ['Gigabyte', 'M32U 4K IPS', 'Monitor', 549, '32" 4K 144Hz IPS gaming monitor', { 'Panel':'IPS', 'Resolution':'3840x2160', 'Refresh':'144Hz', 'Response':'1ms', 'Inputs':'HDMI 2.1 x2, DP 1.4, USB-C'}],

  // Headsets
  ['SteelSeries', 'Arctis Nova Pro Wireless', 'Headset', 349, 'Hi-res wireless gaming headset with hot-swap battery', { 'Drivers':'40mm Neodymium', 'Connectivity':'2.4GHz / Bluetooth', 'Battery':'Hot-swap dual', 'Mic':'AI Noise Cancelling'}],
  ['Razer', 'BlackShark V2 Pro 2023', 'Headset', 199, 'Esports wireless headset with TriForce drivers', { 'Drivers':'50mm TriForce Titanium', 'Connectivity':'2.4GHz / Bluetooth', 'Battery':'70h', 'Mic':'HyperClear Super Wideband'}],
  ['Logitech', 'G Pro X 2 Lightspeed', 'Headset', 249, 'Esports wireless headset with graphene drivers', { 'Drivers':'50mm Graphene', 'Connectivity':'Lightspeed / BT / 3.5mm', 'Battery':'50h', 'Mic':'Detachable boom'}],
  ['HyperX', 'Cloud III Wireless', 'Headset', 169, 'Long-battery wireless gaming headset', { 'Drivers':'53mm Dynamic', 'Connectivity':'2.4GHz / USB-C', 'Battery':'120h', 'Mic':'Detachable boom'}],
  ['Audeze', 'Maxwell Wireless', 'Headset', 329, 'Planar magnetic wireless gaming headset', { 'Drivers':'90mm Planar Magnetic', 'Connectivity':'2.4GHz / BT / USB-C', 'Battery':'80h', 'Mic':'Broadcast quality'}],

  // Cases
  ['Lian Li', 'O11 Dynamic EVO RGB', 'Case', 199, 'Dual-chamber tempered glass mid-tower with ARGB', { 'Form Factor':'Mid-Tower', 'Material':'SECC + Tempered Glass', 'Drive Bays':'6x 2.5", 4x 3.5"', 'Fans Included':'4x 120mm ARGB'}],
  ['Fractal Design', 'North XL', 'Case', 169, 'Walnut-accent airflow tower', { 'Form Factor':'Full-Tower', 'Material':'Steel + Walnut', 'Drive Bays':'4x 2.5", 4x 3.5"', 'Fans Included':'3x 140mm'}],
  ['NZXT', 'H7 Flow RGB+', 'Case', 159, 'Airflow-focused mid-tower with smart fans', { 'Form Factor':'Mid-Tower', 'Material':'Steel + Tempered Glass', 'Drive Bays':'5x 2.5", 2x 3.5"', 'Fans Included':'3x 140mm F-series RGB'}],
  ['Hyte', 'Y70 Touch', 'Case', 359, 'Panoramic dual-chamber case with built-in touch display', { 'Form Factor':'Mid-Tower', 'Material':'Steel + Glass', 'Display':'14.1" 1100x3840 IPS touch', 'Fans Included':'4x 140mm'}],

  // Cooling
  ['NZXT', 'Kraken Elite 360 RGB', 'Cooling', 329, '360mm AIO with 2.36" LCD', { 'Type':'AIO 360mm', 'Pump':'7th-gen Asetek', 'Display':'2.36" 24-bit IPS', 'Fans':'3x F140 RGB Core'}],
  ['Corsair', 'iCUE H170i Elite LCD', 'Cooling', 369, '420mm AIO with LCD pump cap', { 'Type':'AIO 420mm', 'Display':'2.1" IPS', 'Fans':'3x AF140 RGB Elite'}],
  ['Noctua', 'NH-D15 G2', 'Cooling', 149, 'Dual-tower air cooler flagship', { 'Type':'Dual-tower air', 'Fans':'2x NF-A14x25 G2', 'Sockets':'LGA1700/1851, AM4/AM5'}],
  ['Arctic', 'Liquid Freezer III 360', 'Cooling', 119, 'High-performance 360mm AIO', { 'Type':'AIO 360mm', 'Pump':'Hub Gen II', 'Fans':'3x P12 PWM PST A-RGB'}],

  // PSUs
  ['Corsair', 'RM1000x Shift 1000W', 'PSU', 229, '80+ Gold modular PSU with side connectors', { 'Wattage':'1000W', 'Efficiency':'80+ Gold', 'Modular':'Fully modular', 'Connectors':'12V-2x6 included'}],
  ['Seasonic', 'Vertex GX-1200 1200W', 'PSU', 269, '80+ Gold ATX 3.0 fully modular PSU', { 'Wattage':'1200W', 'Efficiency':'80+ Gold', 'Modular':'Fully modular', 'Connectors':'12V-2x6 included'}],
  ['be quiet!', 'Dark Power Pro 13 1300W', 'PSU', 379, '80+ Titanium high-end ATX 3.0 PSU', { 'Wattage':'1300W', 'Efficiency':'80+ Titanium', 'Modular':'Fully modular', 'Connectors':'12V-2x6 included'}],

  // Chairs & desks
  ['Secretlab', 'TITAN Evo 2022', 'Chair', 549, 'Premium ergonomic gaming chair', { 'Material':'Neo Hybrid Leatherette', 'Capacity':'130 kg', 'Recline':'165 degrees', 'Lumbar':'4-way adjustable'}],
  ['Herman Miller', 'Embody Gaming', 'Chair', 1795, 'Ergonomic gaming office chair', { 'Material':'Cybernetic Pixelated Support', 'Recline':'18°', 'Warranty':'12 years'}],

  // Mousepads & misc
  ['Razer', 'Strider Chroma XXL', 'Mousepad', 79, 'RGB hybrid hard/soft XXL mousepad', { 'Size':'900x400x4mm', 'Surface':'Micro-textured cloth', 'Lighting':'Chroma RGB'}],
  ['Logitech', 'G840 XL', 'Mousepad', 39, 'XL cloth gaming mousepad', { 'Size':'900x400x3mm', 'Surface':'Cloth'}],
  ['Stream Deck', 'Stream Deck XL', 'Streaming', 249, '32-key customizable controller', { 'Keys':'32 customizable LCD', 'Connection':'USB-C', 'Software':'Stream Deck'}]
];

peripherals.forEach(([brand, model, subcat, price, desc, specs]) => {
  add({
    category: 'Peripheral',
    subCategory: subcat,
    brand, model, price: round2(price),
    short: desc.length > 60 ? desc.slice(0, 60) + '...' : desc,
    description: `${brand} ${model}. ${desc}`,
    specs,
    benchmark: {},
    tags: ['gaming']
  });
});

// Verify count and write
console.error(`Generated ${products.length} products`);
const byCat = {};
products.forEach(p => { byCat[p.category] = (byCat[p.category] || 0) + 1; });
console.error('By category:', byCat);

const out = path.join(__dirname, 'assets', 'products.json');
fs.writeFileSync(out, JSON.stringify({ products }, null, 0));
console.log('Wrote', out, fs.statSync(out).size, 'bytes');
