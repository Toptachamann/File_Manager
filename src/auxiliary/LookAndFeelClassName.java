package auxiliary;

public enum LookAndFeelClassName {
    METAL_LOOK_AND_FEEL("javax.swing.plaf.metal.MetalLookAndFeel"),
    NIMBUS_LOOK_AND_FEEL("javax.swing.plaf.nimbus.NimbusLookAndFeel"),
    MOTIF_LOOK_AND_FEEL("com.sun.java.swing.plaf.motif.MotifLookAndFeel"),
    WINDOWS_LOOK_AND_FEEL("com.sun.java.swing.plaf.windows.WindowsLookAndFeel"),
    WINDOWS_CLASSIC_LOOK_AND_FEEL("com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel");

    private final String className;

    LookAndFeelClassName(String name) {
        this.className = name;
    }

    public String className() {
        return this.className;
    }


}
