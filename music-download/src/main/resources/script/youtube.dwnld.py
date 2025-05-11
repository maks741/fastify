import argparse
import yt_dlp
import json
import os
import sys

def download_audio(url, download_path, audio_format, thumbnail_format):
  options = {
        'extract_audio': True,
        'writethumbnail': True,
        'format': 'bestaudio',
        'outtmpl': download_path + '%(uploader)s^%(title)s/%(uploader)s^%(title)s',
        'postprocessors': [
            {
                'key': 'FFmpegExtractAudio',
                'preferredcodec': audio_format
            },
            {
                'key': 'FFmpegThumbnailsConvertor',
                'format': thumbnail_format,
            }
        ],
        'quiet': True
  }

  with yt_dlp.YoutubeDL(options) as ydl:
    info = ydl.extract_info(url, download=True)
    uploader = info.get("uploader")
    title = info.get("title")
    relative_download_path = download_path + uploader + '^' + title + '/' + uploader + '^' + title
    base_output_path = os.path.abspath(relative_download_path)
    output = {
       "videoId": info.get("id"),
       "uploader": uploader,
       "title": title,
       "audioPath": base_output_path + '.' + audio_format,
       "thumbnailPath": base_output_path + '.' + thumbnail_format
    }
    sys.stdout.write(json.dumps(output, ensure_ascii=False, indent=2))


if __name__ == '__main__':
    arg_parser = argparse.ArgumentParser()
    arg_parser.add_argument("url", help="URL of the youtube video", type=str)
    arg_parser.add_argument("path", help="Path to download to", type=str)
    arg_parser.add_argument("audio", help="Audio format", type=str)
    arg_parser.add_argument("thumbnail", help="Thumbnail format", type=str)

    args = arg_parser.parse_args()

    url = args.url
    download_path = args.path
    audio_format = args.audio
    thumbnail_format = args.thumbnail

    download_audio(url, download_path, audio_format, thumbnail_format)
