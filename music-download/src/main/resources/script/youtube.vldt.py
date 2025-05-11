import argparse
import yt_dlp
import json
import sys


def extract_info(url):
  options = {
        'quiet': True
  }

  with yt_dlp.YoutubeDL(options) as ydl:
    info = ydl.extract_info(url, download=False)
    filesize = info.get("filesize") or info.get("filesize_approx")
    output = {
       "videoId": info.get("id"),
       "uploader": info.get("uploader"),
       "title": info.get("title"),
       "filesize": filesize,
       "filesizeMb": filesize / 1_000_000
    }
    sys.stdout.write(json.dumps(output, ensure_ascii=False, indent=2))


if __name__ == '__main__':
    arg_parser = argparse.ArgumentParser()
    arg_parser.add_argument("url", help="URL of the youtube video", type=str)

    args = arg_parser.parse_args()

    url = args.url

    extract_info(url)
