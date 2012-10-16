%global _binary_filedigest_algorithm 1
%global _source_filedigest_algorithm 1
%global _binary_payload w9.gzdio
%global _source_payload w9.gzdio

%global selinux_variants mls strict targeted
%global selinux_policyver %(%{__sed} -e 's,.*selinux-policy-\\([^/]*\\)/.*,\\1,' /usr/share/selinux/devel/policyhelp || echo 0.0.0)
%global modulename thumbslug


Name: splice-certmaker
Summary: splice-certmaker summary
Group: Internet/Applications
License: GPLv2
Version: 0.0.3
Release: 1%{?dist}
URL: https://github.com/splice/splice-certgen
Source: %{name}-%{version}.tar.gz
BuildRoot: %{_tmppath}/%{name}-%{version}-%{release}-buildroot
Vendor: Red Hat, Inc.
BuildArch: noarch

Requires(pre): shadow-utils
Requires: log4j >= 1.2
Requires: java >= 1.6.0

BuildRequires: ant >= 1.7.0
BuildRequires: log4j >= 1.2
BuildRequires: jakarta-commons-codec
BuildRequires: java-devel >= 1.6.0

#jetty-util

%define __jar_repack %{nil}

%description
splice-certmaker desc


%package selinux
Summary:        SELinux policy module supporting splice-certmaker
Group:          System Environment/Base
BuildRequires:  checkpolicy
BuildRequires:  selinux-policy-devel
BuildRequires:  /usr/share/selinux/devel/policyhelp
BuildRequires:  hardlink

%if "%{selinux_policyver}" != ""
Requires:       selinux-policy >= %{selinux_policyver}
%endif
Requires:       %{name} = %{version}-%{release}
Requires(post):   /usr/sbin/semodule
Requires(post):   /sbin/restorecon
Requires(postun): /usr/sbin/semodule
Requires(postun): /sbin/restorecon


%description selinux
SELinux policy module supporting splice-certmaker


%prep
%setup -q 

%build
ant -Dlibdir=/usr/share/java clean package

cd selinux
for selinuxvariant in %{selinux_variants}
do
  make NAME=${selinuxvariant} -f /usr/share/selinux/devel/Makefile
  mv %{modulename}.pp %{modulename}.pp.${selinuxvariant}
  make NAME=${selinuxvariant} -f /usr/share/selinux/devel/Makefile clean
done
cd -


%install
install -d -m 755 $RPM_BUILD_ROOT/%{_datadir}/%{name}/
install -m 644 target/%{name}.jar $RPM_BUILD_ROOT/%{_datadir}/%{name}

install -d -m 755 $RPM_BUILD_ROOT/%{_bindir}/
install -m 755 %{name}.bin $RPM_BUILD_ROOT/%{_bindir}/%{name}

install -d -m 755 $RPM_BUILD_ROOT/%{_initddir}
install -m 755 splice-certmaker.init $RPM_BUILD_ROOT/%{_initddir}/%{name}

install -d -m 755 $RPM_BUILD_ROOT/%{_sysconfdir}/splice-certmaker
install -m 640 splice-certmaker.conf \
            $RPM_BUILD_ROOT/%{_sysconfdir}/splice-certmaker/splice-certmaker.conf

install -d -m 775 $RPM_BUILD_ROOT/%{_var}/log/splice-certmaker
install -d -m 775 $RPM_BUILD_ROOT/%{_var}/run/splice-certmaker

cd selinux
for selinuxvariant in %{selinux_variants}
do
  install -d $RPM_BUILD_ROOT/%{_datadir}/selinux/${selinuxvariant}
  install -p -m 644 %{modulename}.pp.${selinuxvariant} \
    $RPM_BUILD_ROOT/%{_datadir}/selinux/${selinuxvariant}/%{modulename}.pp
done
cd -
/usr/sbin/hardlink -cv $RPM_BUILD_ROOT/%{_datadir}/selinux

%clean
rm -rf $RPM_BUILD_ROOT


%pre
getent group splice >/dev/null || groupadd -r splice
getent passwd splice >/dev/null || \
    useradd -r -g splice -d %{_datadir}/%{name} -s /sbin/nologin \
    -c "splice user" splice
exit 0


%post
/sbin/chkconfig --add %{name}


%postun
if [ "$1" -ge "1" ] ; then
    /sbin/service %{name} condrestart >/dev/null 2>&1 || :
fi


%preun
if [ $1 -eq 0 ] ; then
    /sbin/service %{name} stop >/dev/null 2>&1
    /sbin/chkconfig --del %{name}
fi


%post selinux
for selinuxvariant in %{selinux_variants}
do
  /usr/sbin/semodule -s ${selinuxvariant} -i \
    %{_datadir}/selinux/${selinuxvariant}/%{modulename}.pp &> /dev/null || :
done
/sbin/restorecon %{_localstatedir}/cache/splice-certmaker || :
/usr/sbin/semanage port -a -t splice-certmaker_port_t -p tcp 8080 || :

%postun selinux
if [ $1 -eq 0 ] ; then
  for selinuxvariant in %{selinux_variants}
  do
     /usr/sbin/semodule -s ${selinuxvariant} -r %{modulename} &> /dev/null || :
  done
  [ -d %{_localstatedir}/cache/splice-certmaker ]  && \
    /sbin/restorecon -R %{_localstatedir}/cache/splice-certmaker &> /dev/null || :
  /usr/sbin/semanage port -a -t splice-certmaker_port_t -p tcp 8080 || :
fi


%files
%defattr(-, root, splice)
%doc README
%{_initddir}/%{name}
%{_bindir}/%{name}

%dir %{_sysconfdir}/splice-certmaker
%config(noreplace) %{_sysconfdir}/%{name}/%{name}.conf

%dir %{_datadir}/%{name}
%{_datadir}/%{name}/splice-certmaker.jar

%dir %{_var}/log/splice-certmaker
%dir %{_var}/run/splice-certmaker
%ghost %attr(660, splice, splice) %{_var}/run/splice/certmaker.pid
%ghost %attr(660, splice, splice) %{_var}/lock/subsys/splice-certmaker

%files selinux
%defattr(-,root,root,0755)
%doc selinux/*
%{_datadir}/selinux/*/%{modulename}.pp


%changelog
* Tue Oct 16 2012 Chris Duryee (beav) <cduryee@redhat.com>
- first cut at building (cduryee@redhat.com)

* Tue Oct 16 2012 Chris Duryee (beav) <cduryee@redhat.com>
- new package built with tito

