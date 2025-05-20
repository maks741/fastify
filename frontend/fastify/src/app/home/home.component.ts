import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { MusicService } from '../services/music.service';
import { MusicItem } from '../models/music.model';
import { FormsModule } from '@angular/forms';

interface AudioData {
  videoId: string;
  url: string;
}

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
  musicItems: MusicItem[] = [];
  isLoading = false;
  error: string | null = null;

  // New music dialog properties
  showDialog = false;
  newMusicUrl = '';
  isAddingMusic = false;
  dropdownVisible = false;

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

  // Dialog methods
  openDialog() {
    this.showDialog = true;
    this.newMusicUrl = '';
  }

  closeDialog() {
    this.showDialog = false;
  }

  addNewMusic() {
    if (!this.newMusicUrl.trim()) {
      return;
    }

    this.isAddingMusic = true;
    this.musicService.addMusic({ url: this.newMusicUrl }).subscribe({
      next: (newItem: MusicItem) => {
        this.musicItems = [...this.musicItems, newItem];
        this.isAddingMusic = false;
        this.closeDialog();
      },
      error: (error) => {
        console.error('Error adding music:', error);
        this.error = 'Failed to add music. Please check the URL and try again.';
        this.isAddingMusic = false;
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

  toggleDropdown(): void {
    this.dropdownVisible = !this.dropdownVisible;
  }

  hideDropdown(): void {
    // Add small delay to allow button click to register before hiding
    setTimeout(() => this.dropdownVisible = false, 100);
  }
}
