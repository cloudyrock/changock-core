package io.changock.runner.spring.util;

import org.springframework.context.annotation.Profile;
import org.springframework.util.StringUtils;

import javax.annotation.Nonnull;
import java.lang.reflect.AnnotatedElement;
import java.util.List;

public final class ProfileUtil {

    private ProfileUtil() {
    }

    private static boolean isNegativeProfile(@Nonnull String profile) {
        return profile.charAt(0) == '!';
    }

    private static boolean containsProfile(@Nonnull List<String> activeProfiles, @Nonnull String profile) {
        return activeProfiles.contains(profile);
    }

    private static boolean containsNegativeProfile(@Nonnull List<String> activeProfiles, @Nonnull String profile) {
        return ProfileUtil.containsProfile(activeProfiles, profile.substring(1));
    }

    public static <T extends AnnotatedElement> boolean matchesActiveSpringProfile(@Nonnull List<String> activeProfiles, @Nonnull T element) {
        if (!element.isAnnotationPresent(Profile.class)) {
            return true; // no-profiled changeset always matches
        }
        boolean containsActiveProfile = false;
        for (String profile : element.getAnnotation(Profile.class).value()) {
            if (StringUtils.isEmpty(profile)) {
                continue;
            }
            if (ProfileUtil.isNegativeProfile(profile)) {
                if (ProfileUtil.containsNegativeProfile(activeProfiles, profile)) {
                    return false;
                }
            } else {
                containsActiveProfile = true;
                if (ProfileUtil.containsProfile(activeProfiles, profile)) {
                    return true;
                }
            }
        }
        return !containsActiveProfile;
    }
}
