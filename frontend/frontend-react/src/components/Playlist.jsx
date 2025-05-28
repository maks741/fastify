function Playlist() {
    const playlist = [
        {
            videoId: 'aboba',
            url: 'aboba',
            thumbnailUrl: 'url',
            uploader: 'aaa',
            title: 'aaaa'
        }
    ]

    return (
        <main class="main-content">
            <div class="header-section">
                <h2>Your Music Collection</h2>
                <button class="btn-new">New</button>
            </div>
            <div class="loading">
                Loading your music collection...
            </div>
            {playlist.length > 0 && (
                <div className="music-grid">
                    {playlist.map((music) => {
                        return (
                            <div className="music-card">
                                <div className="thumbnail">
                                    <img src={music.thumbnailUrl} alt={music.title}/>
                                </div>
                                <div className="music-info">
                                    <p className="music-title">{music.title}</p>
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