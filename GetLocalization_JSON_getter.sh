#!/bin/bash

# ---- config ----
DIR=`pwd`
ASSETS="$DIR/dentex.youtube.downloader/assets"
# ----------------

LANG_FILE=$ASSETS/languages

ID=dentex
PW=`zenity --entry --text="GetLocalization password:" --hide-text`
PWexit=$?

if [ "$PWexit" -ne 0 ]; then
	exit
fi

curl --user $ID:$PW https://api.getlocalization.com/ytdownloader/api/translations/list/json/ | \
	sed 's/}, {/}\n }/g' | awk '{print $4}' | sed 's/["|",]//g' | sort -u | tee $LANG_FILE

for i in `cat $LANG_FILE`; do
	curl --user $ID:$PW https://api.getlocalization.com/ytdownloader/api/translators/$i/json/ | \

	if [ "$i" != "it" ]; then	
		sed -e 's/{"username": "dentex".*username=dentex"}, //' -e 's/{"username": "tetter".*username=tetter"}, //' > $ASSETS/$i;
	else
		sed 's/{"username": "tetter".*username=tetter"}, //' > $ASSETS/$i;
	fi
done
