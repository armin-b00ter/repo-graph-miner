Summary:   Java comparison application
Name:      diffj
Version:   1.1.4
Release:   1
Epoch:     0
License:   BSD
Group:     Development/Tools
URL:       http://www.incava.org/projects/diffj
Source:    http://www.incava.org/projects/diffj/diffj-%{version}.tar.gz
BuildRoot: %{_tmppath}/%{name}-%{version}-root
Packager:  Jeff Pace (jpace@incava.org)
Vendor:    incava.org
BuildArch: i386

%description
DiffJ compares Java files based on their Java content, ignoring format,
whitespace and comments.

%prep
%setup -q

%build
ant

%install
[ "$RPM_BUILD_ROOT" -a "$RPM_BUILD_ROOT" != / ] && rm -rf "$RPM_BUILD_ROOT"
ant -Ddestdir=$RPM_BUILD_ROOT install

%clean
[ "$RPM_BUILD_ROOT" -a "$RPM_BUILD_ROOT" != / ] && rm -rf "$RPM_BUILD_ROOT"

%files
%defattr(0644,root,root,0755)
%doc AUTHORS ChangeLog COPYING INSTALL NEWS README
%attr(0755,root,root) %{_bindir}/*
%{_mandir}/*/*
%attr(0755,root,root) %{_datadir}/diffj/diffj.jar

%changelog
* Sun Apr 28 2009 Jeff Pace <jpace@incava.org> 1.1.4-1
- Fixed NPE when displaying output for changed imports and no types declared.
  (Thanks, Frank.)
- Added handling of 1.3, 1.4, 1.5, and 1.6 source.
- Changed default version to 1.5.

* Thu Nov 15 2007 Jeff Pace <jpace@incava.org> 1.1.3-1
- Changed so that output of code added and deleted more closely matches diff.

* Fri Aug 10 2007 Jeff Pace <jpace@incava.org> 1.1.2-1
- Fix to difference comparator of functions (methods and constructors).
- Changed the default Java source to 1.5.

* Thu Aug 17 2006 Jeff Pace <jpace@incava.org> 1.1.1-1
- Fix to difference algorithm.
- Improved handling of parsing/tokenizing errors.

* Tue Aug 08 2006 Jeff Pace <jpace@incava.org> 1.1.0-1
- Rewritten to process Java 1.3, 1.4, and 1.5.
- Significant performance improvements.

* Fri Jul 21 2006 Jeff Pace <jpace@incava.org> 1.0.7-1
- Significant performance improvement.
- Modified startup script to allocate more heap size.

* Tue Jul 18 2006 Jeff Pace <jpace@incava.org> 1.0.6-1
- Fixed bug in diff algorithm.

* Thu Sep 29 2005 Jeff Pace <jpace@incava.org> 1.0.5-1
- Significant performance improvement for large files.

* Mon Jul  4 2005 Jeff Pace <jpace@incava.org> 1.0.1-1
- Refined diff code for separate public release.
- Improved documentation.
- Added javadoc generation.

* Thu Jun  9 2005 Jeff Pace <jpace@incava.org> 1.0.0-1
- Initial version.
