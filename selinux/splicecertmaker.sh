#!/bin/sh -e
# local install for splicecertmaker policy (if you're doing dev work or whatever)


DIRNAME=`dirname $0`
cd $DIRNAME
USAGE="$0 [ --update ]"
if [ `id -u` != 0 ]; then
echo 'You must be root to run this script'
exit 1
fi

if [ $# -eq 1 ]; then
	if [ "$1" = "--update" ] ; then
		time=`ls -l --time-style="+%x %X" splicecertmaker.te | awk '{ printf "%s %s", $6, $7 }'`
		rules=`ausearch --start $time -m avc --raw -se splicecertmaker`
		if [ x"$rules" != "x" ] ; then
			echo "Found avc's to update policy with"
			echo -e "$rules" | audit2allow -R
			echo "Do you want these changes added to policy [y/n]?"
			read ANS
			if [ "$ANS" = "y" -o "$ANS" = "Y" ] ; then
				echo "Updating policy"
				echo -e "$rules" | audit2allow -R >> splicecertmaker.te
				# Fall though and rebuild policy
			else
				exit 0
			fi
		else
			echo "No new avcs found"
			exit 0
		fi
	else
		echo -e $USAGE
		exit 1
	fi
elif [ $# -ge 2 ] ; then
	echo -e $USAGE
	exit 1
fi

echo "Building and Loading Policy"
set -x
make -f /usr/share/selinux/devel/Makefile || exit
/usr/sbin/semodule -i splicecertmaker.pp

/sbin/restorecon -F -R -v /usr/bin/splice-certmaker
/sbin/restorecon -F -R -v /etc/rc.d/init.d/splice-certmaker
/sbin/restorecon -F -R -v /var/lock/subsys/splice-certmaker
/sbin/restorecon -F -R -v /var/log/splice/splice-certmaker.log
/sbin/restorecon -F -R -v /var/run/splice/certmaker.pid
/usr/sbin/semanage port -a -t splicecertmaker_port_t -p tcp 8082
