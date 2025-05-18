import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { MusicService } from '../services/music.service';
import { MusicItem } from '../models/music.model';
import { API_ENDPOINTS } from '../constants/api.constants';

interface AudioData {
  videoId: string;
  url: string;
}

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="home-container">
      <header class="header">
        <div class="logo">fastify</div>
        <button (click)="logout()" class="btn-logout">Logout</button>
      </header>

      <main class="main-content">
        <h1>Your Music Collection</h1>

        <div *ngIf="isLoading" class="loading">
          Loading your music collection...
        </div>

        <div *ngIf="error" class="error-message">
          {{ error }}
        </div>

        <div class="music-grid" *ngIf="!isLoading && !error">
          <div class="music-card" *ngFor="let item of musicItems" (click)="onCardClick(item)">
            <div class="thumbnail">
              <img [src]="item.thumbnailUrl" [alt]="item.title">
              <div class="now-playing-indicator" *ngIf="currentPlayingId === item.videoId">
                <div class="playing-animation">
                  <span></span>
                  <span></span>
                  <span></span>
                </div>
              </div>
            </div>
            <div class="music-info">
              <h3 class="music-title">{{ item.title }}</h3>
              <p class="music-uploader">{{ item.uploader }}</p>
            </div>
          </div>
        </div>
      </main>

      <footer class="audio-player" *ngIf="currentAudio">
        <div class="audio-info">
          <div class="audio-thumbnail" *ngIf="currentTrack">
            <img [src]="currentTrack.thumbnailUrl" [alt]="currentTrack.title">
          </div>
          <div class="audio-details">
            <div class="audio-title">{{ currentTrack?.title || 'Unknown Track' }}</div>
            <div class="audio-artist">{{ currentTrack?.uploader || 'Unknown Artist' }}</div>
          </div>
        </div>

        <div class="audio-controls">
          <button class="control-btn" (click)="togglePlay()">
            <span *ngIf="isPlaying">⏸️</span>
            <span *ngIf="!isPlaying">▶️</span>
          </button>

          <div class="progress-container">
            <span class="current-time">{{ formatTime(currentTime) }}</span>
            <div class="progress-bar-container" (click)="seekAudio($event)">
              <div class="progress-bar" [style.width.%]="(currentTime / duration) * 100"></div>
            </div>
            <span class="duration">{{ formatTime(duration) }}</span>
          </div>

          <div class="volume-control">
            <button class="volume-btn" (click)="toggleMute()">
              <span *ngIf="isMuted">🔇</span>
              <span *ngIf="!isMuted">🔊</span>
            </button>
            <input
              type="range"
              min="0"
              max="1"
              step="0.01"
              [value]="volume"
              (input)="changeVolume($event)"
              class="volume-slider"
            >
          </div>
        </div>
      </footer>
    </div>
  `,
  styles: [`
    .home-container {
      display: flex;
      flex-direction: column;
      min-height: 100vh;
    }

    .header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 1rem 2rem;
      background-color: #111;
      color: white;
      box-shadow: 0 2px 5px rgba(0,0,0,0.1);
    }

    .logo {
      font-size: 1.8rem;
      font-weight: 700;
      color: #1DB954;
      letter-spacing: 1px;
    }

    .btn-logout {
      background-color: transparent;
      border: 1px solid #fff;
      color: white;
      padding: 8px 15px;
      border-radius: 20px;
      cursor: pointer;
      font-size: 14px;
      transition: all 0.2s;
    }

    .btn-logout:hover {
      background-color: white;
      color: #111;
    }

    .main-content {
      flex: 1;
      padding: 2rem;
      background-color: #f5f5f5;
      padding-bottom: 100px; /* Make room for audio player */
    }

    h1 {
      margin-bottom: 2rem;
      font-size: 2rem;
      color: #333;
    }

    .loading {
      text-align: center;
      padding: 2rem;
      color: #666;
      font-size: 1.2rem;
    }

    .error-message {
      color: #dc3545;
      padding: 1rem;
      text-align: center;
      background-color: rgba(220, 53, 69, 0.1);
      border-radius: 8px;
    }

    .music-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
      gap: 20px;
    }

    .music-card {
      background-color: white;
      border-radius: 8px;
      overflow: hidden;
      box-shadow: 0 3px 10px rgba(0,0,0,0.1);
      transition: transform 0.2s, box-shadow 0.2s;
      cursor: pointer;
      position: relative;
    }

    .music-card:hover {
      transform: translateY(-5px);
      box-shadow: 0 5px 15px rgba(0,0,0,0.15);
    }

    .thumbnail {
      width: 100%;
      height: 150px;
      overflow: hidden;
      position: relative;
    }

    .thumbnail img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }

    .now-playing-indicator {
      position: absolute;
      bottom: 0;
      left: 0;
      right: 0;
      background-color: rgba(29, 185, 84, 0.8);
      color: white;
      text-align: center;
      padding: 4px 0;
      font-size: 12px;
    }

    .playing-animation {
      display: flex;
      justify-content: center;
      align-items: center;
      height: 20px;
    }

    .playing-animation span {
      display: inline-block;
      width: 3px;
      height: 10px;
      margin: 0 2px;
      background-color: white;
      border-radius: 2px;
      animation: soundBars 1.2s infinite ease-in-out;
    }

    .playing-animation span:nth-child(2) {
      animation-delay: 0.2s;
    }

    .playing-animation span:nth-child(3) {
      animation-delay: 0.4s;
    }

    @keyframes soundBars {
      0% { height: 5px; }
      50% { height: 15px; }
      100% { height: 5px; }
    }

    .music-info {
      padding: 1rem;
    }

    .music-title {
      margin: 0 0 0.5rem 0;
      font-size: 1rem;
      color: #333;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
    }

    .music-uploader {
      margin: 0;
      font-size: 0.875rem;
      color: #666;
    }

    /* Audio player styles */
    .audio-player {
      position: fixed;
      bottom: 0;
      left: 0;
      right: 0;
      background-color: #111;
      color: white;
      padding: 12px 20px;
      display: flex;
      align-items: center;
      justify-content: space-between;
      box-shadow: 0 -2px 10px rgba(0,0,0,0.2);
      z-index: 100;
      height: 80px;
    }

    .audio-info {
      display: flex;
      align-items: center;
      width: 25%;
    }

    .audio-thumbnail {
      width: 50px;
      height: 50px;
      margin-right: 10px;
      border-radius: 4px;
      overflow: hidden;
      flex-shrink: 0;
    }

    .audio-thumbnail img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }

    .audio-details {
      overflow: hidden;
    }

    .audio-title {
      font-weight: 600;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
      margin-bottom: 2px;
    }

    .audio-artist {
      font-size: 0.8rem;
      color: #ccc;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
    }

    .audio-controls {
      display: flex;
      align-items: center;
      flex: 1;
      justify-content: center;
      margin: 0 20px;
    }

    .control-btn {
      background: none;
      border: none;
      color: white;
      font-size: 24px;
      cursor: pointer;
      padding: 0 15px;
      display: flex;
      align-items: center;
      justify-content: center;
    }

    .progress-container {
      display: flex;
      align-items: center;
      flex: 1;
      margin: 0 15px;
    }

    .current-time, .duration {
      font-size: 12px;
      width: 40px;
      text-align: center;
    }

    .progress-bar-container {
      flex: 1;
      height: 4px;
      background-color: #333;
      margin: 0 10px;
      position: relative;
      cursor: pointer;
      border-radius: 2px;
    }

    .progress-bar {
      height: 100%;
      background-color: #1DB954;
      border-radius: 2px;
    }

    .volume-control {
      display: flex;
      align-items: center;
      margin-left: 15px;
    }

    .volume-btn {
      background: none;
      border: none;
      color: white;
      cursor: pointer;
      padding: 0 5px;
    }

    .volume-slider {
      width: 80px;
      cursor: pointer;
    }
  `]
})
export class HomeComponent implements OnInit {
  musicItems: MusicItem[] = [];
  isLoading = false;
  error: string | null = null;

  // Audio player properties
  @ViewChild('audioPlayer') audioPlayerRef!: ElementRef<HTMLAudioElement>;
  currentAudio: HTMLAudioElement | null = null;
  currentPlayingId: string | null = null;
  currentTrack: MusicItem | null = null;
  isPlaying = false;
  isMuted = false;
  volume = 1;
  currentTime = 0;
  duration = 0;

  constructor(
    private authService: AuthService,
    private musicService: MusicService,
    private router: Router
  ) {}

  ngOnInit() {
    this.loadMusicItems();
    // Create audio element
    this.currentAudio = new Audio();
    this.setupAudioEventListeners();
  }

  loadMusicItems() {
    this.isLoading = true;
    this.error = null;

    this.musicService.getUserMusic().subscribe({
      next: (response) => {
        this.musicItems = response;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error fetching music:', error);
        this.error = 'Failed to load your music collection. Please try again later.';
        this.isLoading = false;
      }
    });
  }

  onCardClick(item: MusicItem) {
    if (!item.videoId) {
      console.error('Video ID not found for item:', item);
      return;
    }

    this.musicService.getAudioUrl(item.videoId).subscribe({
      next: (audioData: AudioData) => {
        this.playAudio(audioData.url, item);
      },
      error: (error) => {
        console.error('Error fetching audio URL:', error);
        this.error = 'Failed to load audio. Please try again later.';
      }
    });
  }

  playAudio(url: string, track: MusicItem) {
    if (!this.currentAudio) return;

    // If the same track is clicked again, toggle play/pause
    if (this.currentPlayingId === track.videoId) {
      this.togglePlay();
      return;
    }

    // Otherwise, load and play the new track
    this.currentAudio.src = url;
    this.currentAudio.load();
    this.currentPlayingId = track.videoId;
    this.currentTrack = track;

    this.currentAudio.play()
      .then(() => {
        this.isPlaying = true;
      })
      .catch(error => {
        console.error('Error playing audio:', error);
      });
  }

  setupAudioEventListeners() {
    if (!this.currentAudio) return;

    this.currentAudio.addEventListener('timeupdate', () => {
      this.currentTime = this.currentAudio?.currentTime || 0;
    });

    this.currentAudio.addEventListener('loadedmetadata', () => {
      this.duration = this.currentAudio?.duration || 0;
    });

    this.currentAudio.addEventListener('ended', () => {
      this.isPlaying = false;
      // Here you could implement auto-play next functionality if desired
    });
  }

  togglePlay() {
    if (!this.currentAudio) return;

    if (this.isPlaying) {
      this.currentAudio.pause();
    } else {
      this.currentAudio.play().catch(error => {
        console.error('Error playing audio:', error);
      });
    }
    this.isPlaying = !this.isPlaying;
  }

  toggleMute() {
    if (!this.currentAudio) return;

    this.currentAudio.muted = !this.currentAudio.muted;
    this.isMuted = this.currentAudio.muted;
  }

  changeVolume(event: Event) {
    if (!this.currentAudio) return;

    const volumeValue = (event.target as HTMLInputElement).value;
    this.volume = parseFloat(volumeValue);
    this.currentAudio.volume = this.volume;

    // If volume is set above 0, ensure muted is turned off
    if (this.volume > 0 && this.isMuted) {
      this.toggleMute();
    }
  }

  seekAudio(event: MouseEvent) {
    if (!this.currentAudio || this.duration === 0) return;

    const progressBar = event.currentTarget as HTMLElement;
    const rect = progressBar.getBoundingClientRect();
    const clickX = event.clientX - rect.left;
    const progressBarWidth = rect.width;
    const seekTime = (clickX / progressBarWidth) * this.duration;

    this.currentAudio.currentTime = seekTime;
  }

  formatTime(seconds: number): string {
    if (isNaN(seconds) || !isFinite(seconds)) return '0:00';

    const minutes = Math.floor(seconds / 60);
    const remainingSeconds = Math.floor(seconds % 60);
    return `${minutes}:${remainingSeconds < 10 ? '0' : ''}${remainingSeconds}`;
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/auth']);
  }

  ngOnDestroy() {
    // Clean up audio element when component is destroyed
    if (this.currentAudio) {
      this.currentAudio.pause();
      this.currentAudio.src = '';
      this.currentAudio.remove();
      this.currentAudio = null;
    }
  }
}
