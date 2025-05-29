import classes from './Playlist.module.css';
import {useState} from "react";

function Playlist() {
    const [isFetching, setIsFetching] = useState(false);

    const playlist = [
        {
            videoId: 'h330RYQFgaY',
            url: 'https://www.youtube.com/watch?v=h330RYQFgaY',
            thumbnailUrl: 'http://localhost:8060/resources/2/h330RYQFgaY/thumbnail.png',
            uploader: 'АДЛИН',
            title: 'Dead Inside (Slowed + Reverb)'
        }
    ]

    return (
        <main className={classes.main_content}>
            <div className={classes.header_section}>
                <h2>Your Music Collection</h2>
                <button className={classes.btn_new}>New</button>
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
                            <div className={classes.music_card}>
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
    )
}

export default Playlist;