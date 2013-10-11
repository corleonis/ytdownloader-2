#!/bin/bash

dest=README_.md

echo "YouTube Downloader for Android _ by dentex
================================================================

Download YouTube video and extract or convert audio to mp3.
For Android 4+ 

*Free and without Ads*

################################################################
" > $dest

code=`grep -oE 'versionCode=".+"' dentex.youtube.downloader/AndroidManifest.xml`

name=`grep -oE 'versionName=".+"' dentex.youtube.downloader/AndroidManifest.xml`

md5=`md5sum dentex.youtube.downloader_v*.apk | sed 's/  /\` /'`

echo '`'$code'`' >> $dest

echo -e "\n\`$name\`" >> $dest

echo -e "\nMD5 checksum: \`"$md5 >> $dest

echo "
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
" >> $dest

echo "
OTHER LICENSES
================================================================
    Additional licenses informations about code used in this project
    is available from within the App's \"About\" menu:
" >> $dest

text0=`sed -n '/string name="credits_text"/,/<\/string>/p' dentex.youtube.downloader/res/values/donottranslate.xml | grep -v -E '\/string|string name'`
echo -e $text0 | sed -e 's/^/    /' -e 's/&#169;/(C)/g' -e 's/&amp;/&/g' -e 's/&#8230;/.../g' -e 's/<http>//g' -e 's/<\/http>//g' -e 's/\\./`/g' >> $dest

echo "
    Note:
    the device-framed screenshots in the project's directory have 
    been generated with the \"Device Frame Generator\" Android App by 
    Prateek Srivastava, available at 
    <https://github.com/f2prateek/Device-Frame-Generator/>.
    The generated artwork is released ander the \"Creative Commons 
    Attribution 3.0 Unported\" license (CC BY).
" >> $dest

echo "
CHANGELOG
================================================================" >> $dest

changelog=`sed -n '/string name="changelog"/,/<\/string>/p' dentex.youtube.downloader/res/values/donottranslate.xml | grep -v -E '\/string|string name'`

echo -e $changelog | sed -e 's/^ \^ /  \^ /g' -e 's/^/    /'>> $dest

echo "
TO-DO LIST
================================================================" >> $dest

cat TODO >> $dest

echo "
KNOWN ISSUES
================================================================" >> $dest

cat KNOWN_ISSUES >> $dest
