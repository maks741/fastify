import classes from './Playlist.module.css';
import {useEffect, useState} from "react";
import Player from "./Player.jsx";
import NewMusic from "./NewMusic.jsx";

function Playlist() {
    const [ isFetching, setIsFetching ] = useState(false);
    const [ isNewMusicDialogOpen, setIsNewMusicDialogOpen ] = useState(false);
    const [ currentTrack, setCurrentTrack ] = useState({});

    const [ playlist, setPlaylist ] = useState([]);

    useEffect(() => {
        setPlaylist([
            {
                videoId: 'h330RYQFgaY',
                url: 'https://www.youtube.com/watch?v=h330RYQFgaY',
                thumbnailUrl: 'http://localhost:8060/resources/2/h330RYQFgaY/thumbnail.png',
                uploader: 'АДЛИН',
                title: 'Dead Inside (Slowed + Reverb)'
            }
        ])
    }, []);

    function onCardClick(musicItem) {
        if (!musicItem.videoId) {
            console.error('Video ID not found for item:', musicItem);
            return;
        }

        const track = {
            ...musicItem,
            audioUrl: 'http://localhost:8060/resources/2/h330RYQFgaY/audio.mp3'
        }

        setCurrentTrack(track);
    }

    function addMusicHandler(musicItem) {
        setPlaylist([...playlist, musicItem]);
    }

    return (
        <>
            {isNewMusicDialogOpen && (
                <NewMusic
                    onClose={() => setIsNewMusicDialogOpen(false)}
                    onAddMusic={addMusicHandler}
                />
            )}

            <main className={classes.main_content}>
                <div className={classes.header_section}>
                    <h2>Your Music Collection</h2>
                    <button className={classes.btn_new} onClick={() => setIsNewMusicDialogOpen(true)}>New</button>
                </div>
                {isFetching && (
                    <div className={classes.loading}>
                        Loading your music collection...
                    </div>
                )}
                {playlist.length > 0 && (
                    <div className={classes.music_grid}>
                        {playlist.map((music) => {
                            return (
                                <div key={music.videoId} className={classes.music_card} onClick={() => onCardClick(music)}>
                                    <div className={classes.thumbnail}>
                                        <img src={music.thumbnailUrl} alt={music.title}/>
                                    </div>
                                    <div className={classes.music_info}>
                                        <p className={classes.music_title}>{music.title}</p>
                                    </div>
                                </div>
                            )
                        })}
                    </div>
                )}
            </main>
            <Player currentTrack={currentTrack}/>
        </>
    )
}

export default Playlist;