package me.tropicalshadow.arcanetable.utils;

import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
public class Version implements Serializable, Comparable<Version> {
    @Serial
    private static final long serialVersionUID = 8687040355286333293L;

    private final int[] version = new int[3];
    private final String postfix;

    public Version(int... version) {
        if (version.length < 2 || version.length > 3)
            throw new IllegalArgumentException("Versions must have 2 or 3 numbers (" + version.length + " numbers given)");
        System.arraycopy(version, 0, this.version, 0, version.length);
        postfix = null;
    }

    private static final Pattern VERSION_PATTERN = Pattern.compile("(\\d+)\\.(\\d+)(?:\\.(\\d+))?(?:-(.*))?");

    public Version(String version) {
        Matcher m = VERSION_PATTERN.matcher(version.trim());
        if (!m.matches())
            throw new IllegalArgumentException("'" + version + "' is not a valid version string");
        for (int i = 0; i < 3; i++) {
            if (m.group(i + 1) != null)
                this.version[i] = Integer.parseInt(m.group(i + 1));
        }
        postfix = m.group(4);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Version))
            return false;
        return compareTo((Version) obj) == 0;
    }

    @Override
    public int hashCode() {
        int result = 31;
        for (int v : version) {
            result = result * 31 + v;
        }
        return result + (postfix == null ? 0 : postfix.hashCode());
    }

    @Override
    public int compareTo(@NotNull Version other) {
        for (int i = 0; i < version.length; i++) {
            if (version[i] > other.version[i])
                return 1;
            if (version[i] < other.version[i])
                return -1;
        }

        if (postfix == null)
            return other.postfix == null ? 0 : 1;
        return other.postfix == null ? -1 : postfix.compareTo(other.postfix);
    }

    public int compareTo(int... other) {
        if (other.length < 2 || other.length > 3)
            throw new IllegalArgumentException("Version must have 2 or 3 numbers (" + other.length + " numbers given)");
        for (int i = 0; i < version.length; i++) {
            if (version[i] > (i >= other.length ? 0 : other[i]))
                return 1;
            if (version[i] < (i >= other.length ? 0 : other[i]))
                return -1;
        }
        return 0;
    }

    private int get(int i) {
        return version[i];
    }

    public boolean isSmallerThan(Version other) {
        return compareTo(other) < 0;
    }

    public boolean isLargerThan(Version other) {
        return compareTo(other) > 0;
    }

    public boolean isStable() {
        return postfix == null;
    }

    public int getMajor() {
        return version[0];
    }

    public int getMinor() {
        return version[1];
    }

    public int getRevision() {
        return version[2];
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(version[0]).append('.').append(version[1]);
        if (version[2] > 0) {
            sb.append('.').append(version[2]);
        }
        if (postfix != null) {
            sb.append('-').append(postfix);
        }
        return sb.toString();
    }

    public static int compare(String v1, String v2) {
        return new Version(v1).compareTo(new Version(v2));
    }
}
