import { useState, useEffect, useRef } from "react";
import classes from './Player.module.css';

const Player = ({ currentTrack }) => {
    const [isPlaying, setIsPlaying] = useState(false);
    const [isMuted, setIsMuted] = useState(false);
    const [volume, setVolume] = useState(1);
    const [currentTime, setCurrentTime] = useState(0);
    const [duration, setDuration] = useState(0);
    const audioRef = useRef(null);

    useEffect(() => {
        if (!audioRef.current) {
            audioRef.current = new Audio();
            setupAudioEventListeners();
        }

        return () => {
            if (audioRef.current) {
                audioRef.current.pause();
                audioRef.current.src = '';
                audioRef.current = null;
            }
        };
    }, []);

    useEffect(() => {
        if (audioRef.current && currentTrack?.audioUrl) {
            audioRef.current.src = currentTrack.audioUrl;
            audioRef.current.load();

            audioRef.current.play()
                .then(() => {
                    setIsPlaying(true);
                })
                .catch(error => {
                    console.error('Error playing audio:', error);
                    setIsPlaying(false);
                });
        }
    }, [currentTrack]);

    const setupAudioEventListeners = () => {
        if (!audioRef.current) return;

        audioRef.current.addEventListener('timeupdate', () => {
            setCurrentTime(audioRef.current?.currentTime || 0);
        });

        audioRef.current.addEventListener('loadedmetadata', () => {
            setDuration(audioRef.current?.duration || 0);
        });

        audioRef.current.addEventListener('ended', () => {
            setIsPlaying(false);
        });

        audioRef.current.addEventListener('play', () => {
            setIsPlaying(true);
        });

        audioRef.current.addEventListener('pause', () => {
            setIsPlaying(false);
        });
    };

    const handleTogglePlay = () => {
        if (!audioRef.current) return;

        if (isPlaying) {
            audioRef.current.pause();
        } else {
            audioRef.current.play().catch(error => {
                console.error('Error playing audio:', error);
            });
        }
    };

    const handleToggleMute = () => {
        if (!audioRef.current) return;

        audioRef.current.muted = !audioRef.current.muted;
        setIsMuted(audioRef.current.muted);
    };

    const handleVolumeChange = (event) => {
        if (!audioRef.current) return;

        const volumeValue = parseFloat(event.target.value);
        setVolume(volumeValue);
        audioRef.current.volume = volumeValue;

        // If volume is set above 0, ensure muted is turned off
        if (volumeValue > 0 && isMuted) {
            audioRef.current.muted = false;
            setIsMuted(false);
        }
    };

    const handleSeekAudio = (event) => {
        if (!audioRef.current || duration === 0) return;

        const progressBar = event.currentTarget;
        const rect = progressBar.getBoundingClientRect();
        const clickX = event.clientX - rect.left;
        const progressBarWidth = rect.width;
        const seekTime = (clickX / progressBarWidth) * duration;

        audioRef.current.currentTime = seekTime;
    };

    const formatTime = (seconds) => {
        if (isNaN(seconds) || !isFinite(seconds)) return '0:00';

        const minutes = Math.floor(seconds / 60);
        const remainingSeconds = Math.floor(seconds % 60);
        return `${minutes}:${remainingSeconds < 10 ? '0' : ''}${remainingSeconds}`;
    };

    const handlePrevious = () => {
        // Previous functionality can be implemented here
    };

    const handleNext = () => {
        // Next functionality can be implemented here
    };

    if (!currentTrack) {
        return null;
    }

    return (
        <footer className={classes.audio_player}>
            <div className={classes.audio_info}>
                <div className={classes.audio_thumbnail}>
                    <img src={currentTrack.thumbnailUrl} alt={currentTrack.title} />
                </div>
                <div className={classes.audio_details}>
                    <div className={classes.audio_title}>{currentTrack.title}</div>
                    <div className={classes.audio_artist}>{currentTrack.uploader}</div>
                </div>
            </div>

            <div className={classes.audio_controls}>
                <div className={classes.player_buttons}>
                    <button className={`${classes.control_btn} ${classes.previous_btn}`} onClick={handlePrevious}>
                        <img src="/assets/images/previous-button.png" alt="Previous" />
                    </button>
                    <button className={`${classes.control_btn} ${classes.play_pause_btn}`} onClick={handleTogglePlay}>
                        {isPlaying ? (
                            <img src="/assets/images/pause-button.png" alt="Pause" />
                        ) : (
                            <img src="/assets/images/play-button.png" alt="Play" />
                        )}
                    </button>
                    <button className={`${classes.control_btn} ${classes.next_btn}`} onClick={handleNext}>
                        <img src="/assets/images/next-button.png" alt="Next" />
                    </button>
                </div>

                <div className={classes.progress_container}>
                    <span className={classes.current_time}>{formatTime(currentTime)}</span>
                    <div className={classes.progress_bar_container} onClick={handleSeekAudio}>
                        <div
                            className={classes.progress_bar}
                            style={{ width: `${(currentTime / duration) * 100}%` }}
                        ></div>
                    </div>
                    <span className={classes.duration}>{formatTime(duration)}</span>
                </div>
            </div>

            <div className={classes.volume_control}>
                <button className={classes.volume_btn} onClick={handleToggleMute}>
                    {isMuted ? (
                        <img src="/assets/images/volume-mute.png" alt="Muted" />
                    ) : (
                        <img src="/assets/images/volume-up.png" alt="Volume" />
                    )}
                </button>
                <input
                    type="range"
                    min="0"
                    max="1"
                    step="0.01"
                    value={volume}
                    onChange={handleVolumeChange}
                    className={classes.volume_slider}
                />
            </div>
        </footer>
    );
};

export default Player;