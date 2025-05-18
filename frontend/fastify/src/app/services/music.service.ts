import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { MusicItem } from '../models/music.model';
import { API_ENDPOINTS } from '../constants/api.constants';
import { AuthService } from './auth.service';

interface AudioData {
  videoId: string;
  url: string;
}

@Injectable({
  providedIn: 'root'
})
export class MusicService {
  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) {}

  private getHttpOptions() {
    const token = this.authService.getToken();
    return {
      headers: new HttpHeaders({
        'Content-Type': 'application/json',
        'Accept': 'application/json',
        'Authorization': token ? `Bearer ${token}` : ''
      }),
      withCredentials: false
    };
  }

  getUserMusic(): Observable<MusicItem[]> {
    return this.http.get<MusicItem[]>(API_ENDPOINTS.USER_MUSIC, this.getHttpOptions());
  }

  getAudioUrl(videoId: string): Observable<AudioData> {
    return this.http.get<AudioData>(`${API_ENDPOINTS.GET_AUDIO_URL}/${videoId}`, this.getHttpOptions());
  }
}
