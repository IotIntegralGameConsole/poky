DESCRIPTION = "LSB support for OpenEmbedded"
SECTION = "console/utils"
HOMEPAGE = "http://prdownloads.sourceforge.net/lsb"
LICENSE = "GPLv2+"
PR = "r0"

# lsb_release needs getopt
RDEPENDS_${PN} += "util-linux"

LIC_FILES_CHKSUM = "file://README;md5=12da544b1a3a5a1795a21160b49471cf"

SRC_URI = "${SOURCEFORGE_MIRROR}/project/lsb/lsb_release/1.4/lsb-release-1.4.tar.gz \
           file://init-functions \
           file://lsb_killproc \
           file://lsb_log_message \
           file://lsb_pidofproc \
           file://lsb_start_daemon \
           "

SRC_URI[md5sum] = "30537ef5a01e0ca94b7b8eb6a36bb1e4"
SRC_URI[sha256sum] = "99321288f8d62e7a1d485b7c6bdccf06766fb8ca603c6195806e4457fdf17172"
S = "${WORKDIR}/lsb-release-1.4"

do_install(){
	oe_runmake install prefix=${D}  mandir=${D}/${datadir}/man/ DESTDIR=${D}

	# this 2 dirs are needed by package lsb-dist-checker
	mkdir -p ${D}${sysconfdir}/opt
	mkdir -p ${D}${localstatedir}/opt

	mkdir -p ${D}${base_bindir}
	mkdir -p ${D}/${baselib}
	mkdir -p ${D}${sysconfdir}/lsb-release.d
	echo -n "LSB_VERSION=\"core-4.1-noarch:" > ${D}${sysconfdir}/lsb-release

	if [ "${TARGET_ARCH}" = "i586" ];then
		echo -n "core-4.1-ia32" >>  ${D}${sysconfdir}/lsb-release
	else
		echo -n "core-4.1-${TARGET_ARCH}" >>  ${D}${sysconfdir}/lsb-release
	fi
	echo "\"" >> ${D}${sysconfdir}/lsb-release
	echo "DISTRIB_ID=${DISTRO}" >> ${D}${sysconfdir}/lsb-release
	echo "DISTRIB_RELEASE=${DISTRO_VERSION}" >> ${D}${sysconfdir}/lsb-release
	echo "DISTRIB_DESCRIPTION=\"${DISTRO_NAME} ${DISTRO_VERSION}\"" >> ${D}${sysconfdir}/lsb-release

	if [ "${TARGET_ARCH}" = "i586" ];then
		mkdir -p ${D}${sysconfdir}/lsb-release.d
		touch ${D}${sysconfdir}/lsb-release.d/graphics-${PV}-noarch
		touch ${D}${sysconfdir}/lsb-release.d/desktop-${PV}-noarch
		touch ${D}${sysconfdir}/lsb-release.d/graphics-${PV}-ia32
		touch ${D}${sysconfdir}/lsb-release.d/desktop-${PV}-ia32
	elif [ "${TARGET_ARCH}" = "x86_64" ];then
		touch ${D}${sysconfdir}/lsb-release.d/graphics-${PV}-noarch
		touch ${D}${sysconfdir}/lsb-release.d/graphics-${PV}-amd64
		touch ${D}${sysconfdir}/lsb-release.d/desktop-${PV}-amd64
	fi
	if [ "${TARGET_ARCH}" = "powerpc" ];then
		touch ${D}${sysconfdir}/lsb-release.d/graphics-${PV}-noarch
		touch ${D}${sysconfdir}/lsb-release.d/graphics-${PV}-ppc32
		touch ${D}${sysconfdir}/lsb-release.d/desktop-${PV}-ppc32
	elif [ "${TARGET_ARCH}" = "powerpc64" ];then
		touch ${D}${sysconfdir}/lsb-release.d/graphics-${PV}-noarch
		touch ${D}${sysconfdir}/lsb-release.d/graphics-${PV}-ppc64
		touch ${D}${sysconfdir}/lsb-release.d/desktop-${PV}-ppc64
	fi
}

do_install_append(){
       install -d ${D}${sysconfdir}/core-lsb
       install -d ${D}/${baselib}/lsb
       for i in lsb_killproc lsb_log_message lsb_pidofproc lsb_start_daemon
       do
           install -m 0755 ${WORKDIR}/${i} ${D}${sysconfdir}/core-lsb
       done
       install -m 0755 ${WORKDIR}/init-functions ${D}/${baselib}/lsb
       if [ "${TARGET_ARCH}" = "x86_64" ];then
	       cd ${D}
               if [ "${baselib}" != "lib64" ]; then
                   ln -sf ${baselib} lib64
               fi
	       cd ${D}/${baselib}
               ln -sf ld-linux-x86-64.so.2 ld-lsb-x86-64.so.2
               ln -sf ld-linux-x86-64.so.2 ld-lsb-x86-64.so.3
       fi
       if [ "${TARGET_ARCH}" = "i586" ];then
	       cd ${D}/${baselib}
               ln -sf ld-linux.so.2 ld-lsb.so.2
               ln -sf ld-linux.so.2 ld-lsb.so.3
       fi

       if [ "${TARGET_ARCH}" = "powerpc64" ];then
               cd ${D}
               if [ "${baselib}" != "lib64" ]; then
                   ln -sf ${baselib} lib64
               fi
               cd ${D}/${baselib}
               ln -sf ld64.so.1 ld-lsb-ppc64.so.2
               ln -sf ld64.so.1 ld-lsb-ppc64.so.3
       fi
       if [ "${TARGET_ARCH}" = "powerpc" ];then
	       cd ${D}/${baselib}
               ln -sf ld.so.1 ld-lsb-ppc32.so.2
               ln -sf ld.so.1 ld-lsb-ppc32.so.3
       fi
}
FILES_${PN} += "/lib64 \
                ${base_libdir}/lsb/* \
               "
