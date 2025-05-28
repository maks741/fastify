import classes from './AppHeader.module.css'

function AppHeader() {
    return (
        <header className={classes.header}>
            <div className={classes.logo}>
                <img src="/assets/images/spotify-logo.png" width="50" height="50" alt="spotify-logo" />
            </div>
            <div className={classes.profile_container}>
                <div className={classes.profile_hover_area}>
                    <img src="/assets/images/profile-icon.png" alt="User Profile" className={classes.profile_icon} />
                </div>
                <div className={classes.dropdown_menu}>
                    <button className={classes.dropdown_item}>Logout</button>
                </div>
            </div>
        </header>
    );
}

export default AppHeader;