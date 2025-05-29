import { useState } from 'react';
import classes from './NewMusic.module.css';
import {API_ENDPOINTS} from "../constants/api.constants.js";
import api from "../service/HttpClient.js";

const NewMusic = ({
                         onClose,
                         onAddMusic
                     }) => {
    const [newMusicUrl, setNewMusicUrl] = useState('');
    const [isAddingMusic, setIsAddingMusic] = useState(false);
    const [error, setError] = useState('');

    const closeDialog = () => {
        setNewMusicUrl('');
        setError('');
        onClose();
    };

    const addNewMusic = async () => {
        if (!newMusicUrl.trim()) {
            return;
        }

        setIsAddingMusic(true);
        setError('');

        await api.post(API_ENDPOINTS.ADD_MUSIC, {
            url: newMusicUrl
        }).then(function (response) {
            console.log('response: ', response);
            setIsAddingMusic(false);
            onAddMusic(response);
            closeDialog();
        }).catch(function (error) {
            console.error('Error adding music:', error);
            setError('Failed to add music. Please check the URL and try again.');
            setIsAddingMusic(false);
        });
    };

    const handleKeyDown = (e) => {
        if (e.key === 'Enter') {
            addNewMusic();
        }
    };

    return (
        <div className={classes.dialog_overlay}>
            <div className={classes.dialog}>
                <div className={classes.dialog_header}>
                    <h2>Add New Music</h2>
                    <button
                        className={classes.btn_close}
                        onClick={closeDialog}
                        disabled={isAddingMusic}
                    >
                        ×
                    </button>
                </div>
                <div className={classes.dialog_content}>
                    {error && (
                        <div className={classes.error_message} style={{ color: 'red', marginBottom: '10px' }}>
                            {error}
                        </div>
                    )}
                    <div className={classes.form_group}>
                        <label htmlFor="musicUrl">Enter YouTube URL:</label>
                        <input
                            type="text"
                            id="musicUrl"
                            value={newMusicUrl}
                            onChange={(e) => setNewMusicUrl(e.target.value)}
                            placeholder="https://..."
                            disabled={isAddingMusic}
                            onKeyDown={handleKeyDown}
                        />
                    </div>
                </div>
                <div className={classes.dialog_footer}>
                    <button
                        className={classes.btn_cancel}
                        onClick={closeDialog}
                        disabled={isAddingMusic}
                    >
                        Cancel
                    </button>
                    <button
                        className={classes.btn_submit}
                        onClick={addNewMusic}
                        disabled={!newMusicUrl.trim() || isAddingMusic}
                    >
                        {!isAddingMusic ? (
                            <span>Add Music</span>
                        ) : (
                            <div className={classes.loading_spinner}></div>
                        )}
                    </button>
                </div>
            </div>
        </div>
    );
};

export default NewMusic;