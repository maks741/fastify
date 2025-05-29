import classes from './Playlist.module.css';
import {useEffect, useState} from "react";
import Player from "./Player.jsx";
import NewMusic from "./NewMusic.jsx";
import api from "../service/HttpClient.js";
import {API_ENDPOINTS} from "../constants/api.constants.js";

function Playlist() {
    const [ isFetching, setIsFetching ] = useState(false);
    const [ error, setError ] = useState(null);
    const [ isNewMusicDialogOpen, setIsNewMusicDialogOpen ] = useState(false);
    const [ currentTrack, setCurrentTrack ] = useState({});

    const [ playlist, setPlaylist ] = useState([]);

    useEffect(() => {
        api.get(API_ENDPOINTS.USER_MUSIC)
            .then(function (response) {
                setPlaylist(response.data);
                setIsFetching(false);
            })
            .catch(function (error) {
               setError('Failed to load playlist');
               console.log(error);
            });
    }, []);

    function onCardClick(musicItem) {
        if (!musicItem.videoId) {
            console.error('Video ID not found for item:', musicItem);
            return;
        }

        api.get(API_ENDPOINTS.GET_AUDIO_URL + '/' + musicItem.videoId)
            .then(function (response) {
                const { videoId, url } = response.data;
                const track = {
                    ...musicItem,
                    audioUrl: url
                }

                setCurrentTrack(track);
            })
            .catch(function (error) {
                setError('Failed to load song')
                console.log(error);
            })
    }

    function addMusicHandler(musicItem) {
        setPlaylist([...playlist, musicItem]);
    }

    if (error) {
        return (
            <>
                <p>{error}</p>
            </>
        )
    }

    return (
        <>
            {isFetching && (
                <p>Loading your playlist</p>
            )}
            {isNewMusicDialogOpen && (
                <NewMusic
                    onClose={() => setIsNewMusicDialogOpen(false)}
                    onAddMusic={addMusicHandler}
                />
            )}
            <main className={classes.main_content}>
                <div className={classes.header_section}>
                    <h2>Your Playlist</h2>
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