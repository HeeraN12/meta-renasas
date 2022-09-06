require u-boot-common.inc
require u-boot.inc

DEPENDS += "lzop-native srecord-native"

UBOOT_URL = "git://github.com/renesas-rcar/u-boot.git"
BRANCH = "v2020.10/rcar-5.1.1.rc2"

SRC_URI = "${UBOOT_URL};branch=${BRANCH}"
SRCREV = "3ec5cec05015d8b290a8d390b0246e1df3865199"
PV = "v2020.10+git${SRCPV}"

SRC_URI_append = " \
    file://0001-net-rswitch-Fix-getting-offset-of-GWTRCi-register.patch \
    file://0002-net-rswitch-Fix-rswitch_modify-function.patch \
    file://0003-ARM-dts-renesas-Add-R8A779F0-S4SK-DTs.patch \
    file://0004-ARM-renesas-Add-R8A779F0-S4SK-board-code.patch \
    file://0005-ARM-dts-renesas-Add-Ethernet-Switch-support-on-S4SK.patch \
"

UBOOT_SREC_SUFFIX = "srec"
UBOOT_SREC ?= "u-boot-elf.${UBOOT_SREC_SUFFIX}"
UBOOT_SREC_IMAGE ?= "u-boot-elf-${MACHINE}-${PV}-${PR}.${UBOOT_SREC_SUFFIX}"
UBOOT_SREC_SYMLINK ?= "u-boot-elf-${MACHINE}.${UBOOT_SREC_SUFFIX}"

do_deploy_append() {
    if [ -n "${UBOOT_CONFIG}" ]
    then
        for config in ${UBOOT_MACHINE}; do
            i=$(expr $i + 1);
            for type in ${UBOOT_CONFIG}; do
                j=$(expr $j + 1);
                if [ $j -eq $i ]
                then
                    type=${type#*_}
                    install -m 644 ${B}/${config}/${UBOOT_SREC} ${DEPLOYDIR}/u-boot-elf-${type}-${PV}-${PR}.${UBOOT_SREC_SUFFIX}
                    cd ${DEPLOYDIR}
                    ln -sf u-boot-elf-${type}-${PV}-${PR}.${UBOOT_SREC_SUFFIX} u-boot-elf-${type}.${UBOOT_SREC_SUFFIX}
                fi
            done
            unset j
        done
        unset i
    else
        install -m 644 ${B}/${UBOOT_SREC} ${DEPLOYDIR}/${UBOOT_SREC_IMAGE}
        cd ${DEPLOYDIR}
        rm -f ${UBOOT_SREC} ${UBOOT_SREC_SYMLINK}
        ln -sf ${UBOOT_SREC_IMAGE} ${UBOOT_SREC_SYMLINK}
        ln -sf ${UBOOT_SREC_IMAGE} ${UBOOT_SREC}
    fi
}

