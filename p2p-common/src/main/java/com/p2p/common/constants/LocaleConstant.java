package com.p2p.common.constants;

/**
 * Defines locale identifiers and message bundle settings used by the application.
 *
 * <p>This utility class is not intended to be instantiated.
 *
 * @author Ringuyen
 * @version 1.0.0
 * @since 2026-10-28
 */
public final class LocaleConstant {

    private LocaleConstant() {
        //prevent installation
    }

    /** Language tag for US English. */
    public static final String LANG_EN_US = "en_US";

    /** Language tag for Vietnamese as used by the application. */
    public static final String LANG_VI_VN = "vi_VN";

    /** Request-header prefix used to select US English. */
    public static final String PREFIX_LANG_EN_US = "en";

    /** Request-header prefix used to select Vietnamese. */
    public static final String PREFIX_LANG_VI_VN = "vi";

    /** Base name of the response-code message bundle. */
    public static final String PATH_BASENAME_RESPONSE_CODE = "i18n/response-code/response-code";

    /** Character encoding used to read message bundle files. */
    public static final String DEFAULT_ENCODING_UTF8 = "UTF-8";
}
