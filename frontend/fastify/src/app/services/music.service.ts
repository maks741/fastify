import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, empty } from 'rxjs';
import { MusicItem } from '../models/music.model';
import { API_ENDPOINTS } from '../constants/api.constants';

interface AudioData {
  videoId: string;
  url: string;
}

interface AddMusicRequest {
  url: string;
}

@Injectable({
  providedIn: 'root'
})
export class MusicService {
  constructor(private http: HttpClient) { }

  getUserMusic(): Observable<MusicItem[]> {
    return this.http.get<MusicItem[]>(API_ENDPOINTS.USER_MUSIC);
    // return empty();
  }

  getAudioUrl(videoId: string): Observable<AudioData> {
    return this.http.get<AudioData>(`${API_ENDPOINTS.GET_AUDIO_URL}/${videoId}`);
  }

  addMusic(request: AddMusicRequest): Observable<MusicItem> {
    return this.http.post<MusicItem>(API_ENDPOINTS.ADD_MUSIC, request);
  }
}
