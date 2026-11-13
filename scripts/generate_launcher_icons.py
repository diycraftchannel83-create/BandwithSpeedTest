#!/usr/bin/env python3
"""Generate Android launcher icon resources from Speedtest.png."""
from pathlib import Path
from PIL import Image

ROOT = Path(__file__).resolve().parents[1]
SOURCE = ROOT / "Speedtest.png"
RES = ROOT / "app" / "src" / "main" / "res"

if not SOURCE.is_file():
    raise SystemExit(f"Icon source not found: {SOURCE}")

with Image.open(SOURCE) as opened:
    source = opened.convert("RGBA")

legacy_sizes = {"mdpi": 48, "hdpi": 72, "xhdpi": 96, "xxhdpi": 144, "xxxhdpi": 192}
adaptive_sizes = {"mdpi": 108, "hdpi": 162, "xhdpi": 216, "xxhdpi": 324, "xxxhdpi": 432}

def render(size: int, safe_zone: bool) -> Image.Image:
    canvas = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    limit = int(size * 2 / 3) if safe_zone else size
    artwork = source.copy()
    artwork.thumbnail((limit, limit), Image.Resampling.LANCZOS)
    canvas.alpha_composite(artwork, ((size - artwork.width) // 2, (size - artwork.height) // 2))
    return canvas

for density, size in legacy_sizes.items():
    folder = RES / f"mipmap-{density}"
    folder.mkdir(parents=True, exist_ok=True)
    image = render(size, safe_zone=False)
    image.save(folder / "ic_launcher.png", optimize=True)
    image.save(folder / "ic_launcher_round.png", optimize=True)

for density, size in adaptive_sizes.items():
    folder = RES / f"mipmap-{density}"
    folder.mkdir(parents=True, exist_ok=True)
    render(size, safe_zone=True).save(folder / "ic_launcher_foreground.png", optimize=True)

# Adaptive foreground resources are generated from the same Speedtest.png source.

print("Launcher icons generated successfully from Speedtest.png.")
