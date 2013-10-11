YouTube Downloader for Android _ by dentex
================================================================

Download YouTube video and extract or convert audio to mp3.
For Android 4+ 

*Free and without Ads*

################################################################

`versionCode="64"`

`versionName="3.0.2"`

MD5 checksum: `fdf3896c6a0cebe3524392f958c505e3` dentex.youtube.downloader_v3.0.2.apk

NOTICE
================================================================
    Complying with the GPL below is mandatory.
    It's not that difficult: just give proper credits 
    to this sources and release your modified ones.

LICENSE
================================================================
Copyright (C) 2012-2013  Samuele Rini

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.


OTHER LICENSES
================================================================
    Additional licenses informations about code used in this project
    is available from within the App's "About" menu:

    	Main Licenses 
     
    This App is released under the GPL-3.0. 
    (YouTubeDownloader for Android Copyright (C) 2012-2013 Samuele Rini) 
     
    The FFmpeg binary (http://www.ffmpeg.org/index.html), 
    used for the audio extraction feature, is released under the GPL-3.0. 
    (FFmpeg version 2.0.1 Copyright (C) 2000-2013 the FFmpeg developers) 
     
    The filechooser library is released under the MIT license. 
    (android-filechooser Copyright (C) 2012 Hai Bison) 
     
    The liblame library, used to compile FFmpeg, is released under the LGPL. 
    A GitHub project, https://github.com/intervigilium/liblame, 
    has been used to compile the lame libraries for Android. 
    (liblame Copyright (C) 2010 Ethan Chen) 
    (LAME Copyright (C) 1999-2007 The LAME Project, 
    Copyright (C) 1999-2001 Mark Taylor, 
    Copyright (C) 1998 Micheal Cheng) 
    (mpglib Copyright (C) 1995-1997 Michael Hipp) 
     
    YTD`s mp3 tags are written with MyID3: a Java ID3 Tag Library 
    (http://www.fightingquaker.com/myid3 and 
    https://sites.google.com/site/eternalsandbox/myid3-for-android)
    released under the Apache license, as its dependency Jakarta Regexp library. 
    (MyID3 Copyright (C) 2008 Charles M. Chen, adapted for Android by Romulus Urakagi Ts`ai) 
     
    Picasso: Image downloading and caching library
    Copyright 2013 Square, Inc. - http://square.github.io/picasso/ 
     
    Launcher and notification icons by Sam Plane: http://samplane123.deviantart.com/, 
    released under the CC BY-NC-ND (http://creativecommons.org/licenses/by-nc-nd/3.0) 
     
    Credits for the custom download manager to Hiroshi Matsunaga (matsuhiro): 
    https://github.com/matsuhiro/AndroidDownloadManger (released "unlicensed"). 
     
     	Code excerpts Licenses 
     
    Stack Overflow (http://stackoverflow.com), a question and answer site for programmers where
    all the content is CC BY-SA 3.0 licensed (see http://creativecommons.org/licenses/by-sa/3.0) 
     
    checkMD5(...) & calculateMD5(...): 
    https://github.com/CyanogenMod/android_packages_apps_CMUpdater
    Copyright (C) 2012 The CyanogenMod Project, licensed under the GNU GPLv2 license 
     
    FfmpegController.java: GPL-3.0 - Copyright (C) 2009, Nathan Freitas, Orbot / The Guardian Project
    http://openideals.com/guardian - https://github.com/guardianproject/android-ffmpeg-java 
     
    Observer.java: reference - https://gist.github.com/shirou/659180 
     
    SectionedAdapter.java: GPL-3.0
    Copyright (C) 2008-2010 CommonsWare, LLC - portions Copyright (C) 2008 Jeffrey Sharkey 
     
    Utils.getCpuInfo(): http://www.roman10.net/how-to-get-cpu-information-on-android/ - by Liu Feipeng 
     
    Utils.scanMedia(...):
    http://www.grokkingandroid.com/adding-files-to-androids-media-library-using-the-mediascanner/
    by Wolfram Rittmeyer 
     
    DashboardAdapter.java & DashboardListItem.java: 
    code adapted from https://github.com/survivingwithandroid/Surviving-with-android/
    by Francesco Azzola (JFrankie) 
     
    Rhino.jar: open-source implementation of JavaScript written entirely in Java;
    https://developer.mozilla.org/en-US/docs/Rhino - license: http://www.mozilla.org/MPL/2.0/ 
     
    Javascript function `decryptSignature` from the Greasemonkey script 
    http://userscripts.org/scripts/show/25105 (MIT License) - by Gantt 
    
     For more details, look for comments in YTD`s Java code. 
     
     	Translations 
    
    For more info go to: 
    www.getlocalization.com/ytdownloader 
    forum.xda-developers.com/showthread.php?p=37708791

    Note:
    the device-framed screenshots in the project's directory have 
    been generated with the "Device Frame Generator" Android App by 
    Prateek Srivastava, available at 
    <https://github.com/f2prateek/Device-Frame-Generator/>.
    The generated artwork is released ander the "Creative Commons 
    Attribution 3.0 Unported" license (CC BY).


CHANGELOG
================================================================
    
     v3.0.2 - Oct 11 2013 
    ----------------------------------- 
    [x] small fix 
     
     v3.0.1 - Oct 10 2013 
    ----------------------------------- 
    [x] layout fix for API 14,15,16 
    [x] other bug fixes 
     
     v3.0 - Oct 08 2013 
    ----------------------------------- 
    [x] Dashboard for interaction 
     with downloaded video: 
     - show status/progress/speed 
     - click to: 
      ^ open video/audio file 
      ^ audio extraction/conversion 
     - long-click to manage files: 
      ^ copy 
      ^ move 
      ^ rename 
      ^ redownload 
      ^ remove 
      ^ delete 
      ^ pause/resume 
    [x] Custom download manager 
     supporting: 
     - pause/resume 
     - YouTube link validation on 
     resume through different 
     networks and/or expire time 
    [x] new translations: 
     - Slovak 
     - Slovenian 
     - Vietnamese 
     - Finnish 
     
     v2.7 - Aug 20 2013 
    ----------------------------------- 
    [x] auto-patch experimental 
     signature changes 
    [x] JB 4.3 target 
     
     v2.6.2 - Aug 08 2013 
    ----------------------------------- 
    [x] experimental signature patch 
     
     v2.6.1 - Jul 26 2013 
    ----------------------------------- 
    [x] option to show resolutions 
     into the video list 
    [x] experimental signature patch 
     
     v2.6 - Jul 17 2013 
    ----------------------------------- 
    [x] fix for exp. signature parsing 
    [x] new translations: 
     - Arabic 
     - Danish 
     - Greek 
     
     v2.5.2 - Jul 14 2013 
    ----------------------------------- 
    [x] auto-fetch gantt\'s script to 
     parse experimental signatures 
     
     v2.5.1 - Jul 12 2013 
    ----------------------------------- 
    [x] experimantal signature patch 
     
     v2.5 - Jul 11 2013 
    ----------------------------------- 
    [x] experimantal signature patch 
     (yes, again) 
    [x] other small bugs fixed 
     
     v2.4 - Jul 02 2013 
    ----------------------------------- 
    [x] new launcher and notification 
     icons by Sam Plane 
     
     v2.3.2 - Jun 28 2013 
    ----------------------------------- 
    [x] small bugs fixed 
     
     v2.3.1 - Jun 27 2013 
    ----------------------------------- 
    [x] experimantal signature patch 
     
     v2.3 - Jun 25 2013 
    ----------------------------------- 
    [x] experimantal signature support 
     (thanks Gantt@userscripts.org) 
    [x] other bug/stability fixes 
     
     v2.2 - May 12 2013 
    ----------------------------------- 
    [x] fixed a bunch of BUUUUUGS !!! 
    [x] handles direct link clicks 
     (use of Intent.ACTION_VIEW) 
     
     v2.1 - May 11 2013 
    ----------------------------------- 
    [x] code optimizations 
    [x] mediaScanner bug fix 
     (thanks Wolfram!) 
    [x] new translations: 
     - Chinese (China) 
     - Chinese (Hong Kong) 
     
     v2.0.6 - May 09 2013 
    ----------------------------------- 
    [x] dialog/toast when Downloads 
     system app is not found 
    [x] Chinese (Taiwan) translation 
     
     v2.0.5 - May 09 2013 
    ----------------------------------- 
    [x] bug fix (FC when Downloads 
     system app is not found)(3) 
     
     v2.0.4 - May 08 2013 
    ----------------------------------- 
    [x] bug fix (FC when Downloads 
     system app is not found)(2) 
    [x] Hungarian translation 
     
     v2.0.3 - May 08 2013 
    ----------------------------------- 
    [x] bug fix (FC when Downloads 
     system app is not found) 
     
     v2.0.2 - May 07 2013 
    ----------------------------------- 
    [x] bug fix 
     
     v2.0.1 - May 06 2013 
    ----------------------------------- 
    [x] bug fix 
     
     v2.0 - May 06 2013 
    ----------------------------------- 
    [x] audio extraction from 
     downloaded video with optional 
     conversion to mp3 (with FFmpeg) 
    [x] PayPal donation menu 
    [x] BugSense integration 
    [x] on the fly language switch 
    [x] fixed locale change to default 
    [x] 3 new option on SSH: 
     - use other ConnectBot forks 
     - use link inside same LAN 
     - SSH-send to long-press menu 
    [x] up-navigation for ativities 
    [x] nice translators list 
    [x] new translations: 
     - German 
     - Hebrew 
     - Polish (Poland) 
     - Portuguese (Brazil) 
     - Russian 
     - Spanish 
     - Western Farsi (Iran) 
     
     v1.9 - Mar 23 2013 
    ----------------------------------- 
    [x] YT web page parsing improved 
    [x] fixed notif. error when remove 
     downloads not yet started 
    [x] 4K and 3D video support 
    [x] re-organized menus 
    [x] on the fly theme switch 
    [x] progress bar for video list 
    [x] option to enable logging 
    [x] Korean translation 
     
     v1.8.2 - Mar 15 2013 
    ----------------------------------- 
    [x] dark/light theme switcher 
    [x] option to show all the file 
     sizes into the video list 
    [x] French translation 
     
     v1.8.1 - Mar 14 2013 
    ----------------------------------- 
    [x] notification bug fix 
     
     v1.8 - Mar 12 2013 
    ----------------------------------- 
    [x] extSdCard support 
    [x] long-press on video list item: 
     menu to copy/share link 
    [x] auto update-check once a day 
    [x] new notification icon 
    [x] landscape orientation support 
    [x] YT video thumbnail preview 
    [x] entire row in v.list clickable 
    [x] Turkish translation 
    [x] Dutch translation 
     
     v1.7 - Mar 03 2012 
    ----------------------------------- 
    [x] translations: 
     - Italian 
     - Portuguese (Portugal) 
    [x] option to force the locale 
     
     v1.6 - Mar 01 2013 
    ----------------------------------- 
    [x] own YTD notification bar 
    [x] options for own and sys notif. 
    [x] added some credits 
     
     v1.5.2 - Feb 25 2013 
    ----------------------------------- 
    [x] better fix on bad video list 
    [x] remove stop download on click 
    [x] open DM on notif. bar click 
     (still not working in CM ROM) 
     
     v1.5.1 - Feb 24 2013 
    ----------------------------------- 
    [x] fixed crash if no net is avail. 
    [x] fixed crash on bad video list 
     
     v1.5 - Feb 22 2013 
    ----------------------------------- 
    [x] online update check 
    [x] option to fetch filesizes 
    [x] stop download on click 
     (maybe not working in CM 10.1) 
    [x] DownloadManager quick links 
    [x] settings and DM overflow menu 
    [x] reordered preferences 
    [x] small fixes 
     
     v1.4 - Jan 29 2013 
    ----------------------------------- 
    [x] licenses and code links 
    [x] Share this App option 
     
     v1.3.1 - Jan 19 2013 
    ----------------------------------- 
    [x] handle again http|https links 
     (thanks Reiner!) 
     
     v1.3 - Jan 18 2013 
    ----------------------------------- 
    [x] share via mobile links support 
     
     v1.2 - Dec 25 2012 
    ----------------------------------- 
    [x] fix for YouTube webpage changes 
     
     v1.1.1 - Dec 16 2012 
    ----------------------------------- 
    [x] preferences fix 
     
     v1.1 - Dec 13 2012 
    ----------------------------------- 
    [x] all text strings exported 
    [x] preferences modification 
    [x] quick start tutorial 
     
     v1.0.1 - Dec 11 2012 
    ----------------------------------- 
    [x] bad typo fixed 
    [x] file-chooser new folder icons 
     
     v1.0 - Dec 11 2012 
    ----------------------------------- 
    [x] FIRST PUBLIC RELEASE 
    [x] send download via SSH 
    [x] JB compatibility 
    [x] SSH send how-to dialog 
    [x] standard download locations 
    [x] file-chooser for download dest. 
    [x] add quality suffix options 
    [x] file renaming options

TO-DO LIST
================================================================

	[ ] implement other FFmpeg functions
	[ ] compile FFmpeg to support x86 Android ABI
	[ ] option for automatic audio extraction after download
	[ ] default format/filter selection for video download
	[ ] use icon/logo to identify codecs available (expand the custom ShareActivity adapter)
	[ ] make the App tablet friendly
	[ ] handle SourceForge servers down; fallback on GitHub for apk download:
	    page: https://github.com/dentex/ytdownloader
	    file: https://github.com/dentex/ytdownloader/blob/master/dentex.youtube.downloader_v.*.apk?raw=true
	[ ] use custom DM for other download tasks (apk update, ffmpeg binary [wip])
	[ ] option to include downloaded videos into dashboard backup (or make menu entry "archive")

KNOWN ISSUES
================================================================
	[x] Downloads running:
		- for a long time (around 20 or 30 minutes)
		AND 
		- without having the Dashboard Activity to the front
		may be forced to PAUSE. In this case a manual resume is needed
