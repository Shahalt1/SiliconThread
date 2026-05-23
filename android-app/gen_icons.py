#!/usr/bin/env python3
"""Generate launcher icon PNGs at various densities for SiliconThread.
Uses only stdlib (struct, zlib) — no external deps.
"""
import os, struct, zlib, math

OUT = os.path.dirname(os.path.abspath(__file__))

def write_png(path, w, h, pixels):
    def chunk(tag, data):
        crc = zlib.crc32(tag + data)
        return struct.pack('>I', len(data)) + tag + data + struct.pack('>I', crc)
    raw = b''
    for y in range(h):
        raw += b'\x00'
        for x in range(w):
            r, g, b, a = pixels[y * w + x]
            raw += bytes((r, g, b, a))
    sig = b'\x89PNG\r\n\x1a\n'
    ihdr = struct.pack('>IIBBBBB', w, h, 8, 6, 0, 0, 0)
    idat = zlib.compress(raw, 9)
    with open(path, 'wb') as f:
        f.write(sig)
        f.write(chunk(b'IHDR', ihdr))
        f.write(chunk(b'IDAT', idat))
        f.write(chunk(b'IEND', b''))

def lerp(a, b, t): return a + (b - a) * t

def make_icon(size):
    # Cyberpunk gradient background with neon "S" thread mark.
    pixels = []
    cx, cy = size / 2.0, size / 2.0
    inner_r = size * 0.42
    for y in range(size):
        for x in range(size):
            # diagonal gradient from deep navy to violet
            t = ((x + y) / (2.0 * size))
            r = int(lerp(5, 18, t))
            g = int(lerp(6, 10, t))
            b = int(lerp(15, 60, t))
            a = 255
            # radial neon glow
            dx, dy = x - cx, y - cy
            dist = math.hypot(dx, dy)
            glow = max(0.0, 1.0 - dist / inner_r)
            r = min(255, int(r + glow * 30))
            g = min(255, int(g + glow * 70))
            b = min(255, int(b + glow * 90))
            # Draw stylized "S" thread - sine wave path going diagonally
            # Convert to a stroke around a parametric S curve
            # S curve: points where y = cy + (size/4) * sin(2*pi * (x - cx) / (size*0.6))
            # over x in [cx - size*0.3, cx + size*0.3]
            for tt_i in range(0, 36):
                tt = tt_i / 35.0
                sx = cx + (tt - 0.5) * size * 0.62
                sy = cy + math.sin(tt * math.pi * 2.0) * size * 0.18
                d = math.hypot(x - sx, y - sy)
                if d < size * 0.045:
                    blend = max(0.0, 1.0 - d / (size * 0.045))
                    # cyan-to-lime neon
                    nr = int(lerp(34, 182, tt))
                    ng = int(lerp(211, 255, tt))
                    nb = int(lerp(238, 56, tt))
                    r = int(lerp(r, nr, blend))
                    g = int(lerp(g, ng, blend))
                    b = int(lerp(b, nb, blend))
                    break
            # Rounded corner mask
            corner = size * 0.18
            mx, my = min(x, size - 1 - x), min(y, size - 1 - y)
            if mx < corner and my < corner:
                d = math.hypot(corner - mx, corner - my)
                if d > corner:
                    a = 0
            pixels.append((r, g, b, a))
    return pixels

sizes = {
    'mipmap-mdpi': 48,
    'mipmap-hdpi': 72,
    'mipmap-xhdpi': 96,
    'mipmap-xxhdpi': 144,
    'mipmap-xxxhdpi': 192,
}

for folder, size in sizes.items():
    dir_path = os.path.join(OUT, 'res', folder)
    os.makedirs(dir_path, exist_ok=True)
    pix = make_icon(size)
    write_png(os.path.join(dir_path, 'ic_launcher.png'), size, size, pix)
    write_png(os.path.join(dir_path, 'ic_launcher_round.png'), size, size, pix)
    print(f'wrote {folder}/ic_launcher.png ({size}x{size})')

print('done')
