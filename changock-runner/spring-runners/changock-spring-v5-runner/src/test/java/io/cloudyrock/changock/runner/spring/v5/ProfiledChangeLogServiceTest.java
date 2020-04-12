package io.cloudyrock.changock.runner.spring.v5;


import io.changock.migration.api.ChangeLogItem;
import io.changock.migration.api.ChangeSetItem;
import io.changock.migration.api.ChangockAnnotationProcessor;
import io.changock.runner.spring.v5.ProfiledChangeLogService;
import io.cloudyrock.changock.runner.spring.v5.profiles.defaultProfiled.DefaultProfiledChangerLog;
import io.cloudyrock.changock.runner.spring.v5.profiles.dev.DevProfiledChangerLog;
import io.cloudyrock.changock.runner.spring.v5.profiles.pro.ProProfiledChangeLog;
import io.cloudyrock.changock.runner.spring.v5.profiles.unprofiled.UnprofiledChangerLog;
import org.junit.Test;
import org.springframework.context.annotation.Profile;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ProfiledChangeLogServiceTest {


    @Test
    public void shouldRunDevProfileAndNonAnnotated() throws NoSuchMethodException {
        ProfiledChangeLogService changeLogService = new ProfiledChangeLogService(
                Collections.singletonList(DevProfiledChangerLog.class.getPackage().getName()),
                "0",
                String.valueOf(Integer.MAX_VALUE),
                Collections.singletonList("dev"),
                new ChangockAnnotationProcessor()
        );

        ChangeLogItem changeLog = changeLogService.fetchChangeLogs().get(0);
        assertEquals(DevProfiledChangerLog.class, changeLog.getType());
        assertEquals(DevProfiledChangerLog.class, changeLog.getInstance().getClass());
        assertEquals("01", changeLog.getOrder());
        assertEquals(2, changeLog.getChangeSetElements().size());

        ChangeSetItem changeSet = changeLog.getChangeSetElements().get(0);
        assertEquals("Pdev1", changeSet.getId());
        assertEquals("testuser", changeSet.getAuthor());
        assertFalse(changeSet.isRunAlways());
        assertEquals(DevProfiledChangerLog.class.getMethod("testChangeSet"), changeSet.getMethod());
        assertNull(changeSet.getMethod().getAnnotation(Profile.class));


        changeSet = changeLog.getChangeSetElements().get(1);
        assertEquals("Pdev4", changeSet.getId());
        assertEquals("testuser", changeSet.getAuthor());
        assertTrue(changeSet.isRunAlways());
        assertEquals(DevProfiledChangerLog.class.getMethod("testChangeSet4"), changeSet.getMethod());
        List<String> profiles = Arrays.asList(changeSet.getMethod().getAnnotation(Profile.class).value());
        assertEquals(1, profiles.size());
        assertTrue(profiles.contains("dev"));
    }

    @Test
    public void shouldRunUnProfiledChangeLog_ifMethodsProfiled_WhenDefaultProfile() throws NoSuchMethodException {
        ProfiledChangeLogService changeLogService = new ProfiledChangeLogService(
                Collections.singletonList(ProProfiledChangeLog.class.getPackage().getName()),
                "0",
                String.valueOf(Integer.MAX_VALUE),
                Collections.singletonList("default"),
                new ChangockAnnotationProcessor()
        );

        ChangeLogItem changeLog = changeLogService.fetchChangeLogs().get(0);
        assertEquals(ProProfiledChangeLog.class, changeLog.getType());
        assertEquals(ProProfiledChangeLog.class, changeLog.getInstance().getClass());
        assertEquals(2, changeLog.getChangeSetElements().size());

        ChangeSetItem changeSet = changeLog.getChangeSetElements().get(0);
        assertEquals("no-profiled", changeSet.getId());
        assertEquals("testuser", changeSet.getAuthor());
        assertFalse(changeSet.isRunAlways());
        assertEquals(ProProfiledChangeLog.class.getMethod("noProfiledMethod"), changeSet.getMethod());
        assertNull(changeSet.getMethod().getAnnotation(Profile.class));


        changeSet = changeLog.getChangeSetElements().get(1);
        assertEquals("no-pro-profiled", changeSet.getId());
        assertEquals("testuser", changeSet.getAuthor());
        assertTrue(changeSet.isRunAlways());
        assertEquals(ProProfiledChangeLog.class.getMethod("noProProfiledMethod"), changeSet.getMethod());
        List<String> profiles = Arrays.asList(changeSet.getMethod().getAnnotation(Profile.class).value());
        assertEquals(1, profiles.size());
        assertTrue(profiles.contains("!pro"));
    }

    @Test
    public void shouldNotRunAnyChangeSet_whenAnotherProfile() {
        ProfiledChangeLogService changeLogService = new ProfiledChangeLogService(
                Collections.singletonList(DevProfiledChangerLog.class.getPackage().getName()),
                "0",
                String.valueOf(Integer.MAX_VALUE),
                Collections.singletonList("anotherProfile"),
                new ChangockAnnotationProcessor()
        );
        assertEquals(0, changeLogService.fetchChangeLogs().size());
    }


    @Test
    public void shouldRunAllChangeSets_WhenNoProfileInvolved() throws NoSuchMethodException {

        ProfiledChangeLogService changeLogService = new ProfiledChangeLogService(
                Collections.singletonList(UnprofiledChangerLog.class.getPackage().getName()),
                "0",
                String.valueOf(Integer.MAX_VALUE),
                Collections.singletonList("anotherProfile"),
                new ChangockAnnotationProcessor()
        );

        ChangeLogItem changeLog = changeLogService.fetchChangeLogs().get(0);
        assertEquals(UnprofiledChangerLog.class, changeLog.getType());
        assertEquals(UnprofiledChangerLog.class, changeLog.getInstance().getClass());
        assertEquals(1, changeLog.getChangeSetElements().size());

        ChangeSetItem changeSet = changeLog.getChangeSetElements().get(0);
        assertEquals("no-profiled", changeSet.getId());
        assertEquals("testuser", changeSet.getAuthor());
        assertFalse(changeSet.isRunAlways());
        assertEquals(UnprofiledChangerLog.class.getMethod("noProfiled"), changeSet.getMethod());
        assertNull(changeSet.getMethod().getAnnotation(Profile.class));
    }


    @Test
    public void shouldRunAllChangeSet_whenDefaultProfile_IfDefaultAndEmptyProfile() throws NoSuchMethodException {
        ProfiledChangeLogService changeLogService = new ProfiledChangeLogService(
                Collections.singletonList(DefaultProfiledChangerLog.class.getPackage().getName()),
                "0",
                String.valueOf(Integer.MAX_VALUE),
                Collections.singletonList("default"),
                new ChangockAnnotationProcessor()
        );

        ChangeLogItem changeLog = changeLogService.fetchChangeLogs().get(0);
        assertEquals(DefaultProfiledChangerLog.class, changeLog.getType());
        assertEquals(DefaultProfiledChangerLog.class, changeLog.getInstance().getClass());
        assertEquals("01", changeLog.getOrder());
        assertEquals(2, changeLog.getChangeSetElements().size());

        ChangeSetItem changeSet = changeLog.getChangeSetElements().get(0);
        assertEquals("default-profiled", changeSet.getId());
        assertEquals("testuser", changeSet.getAuthor());
        assertFalse(changeSet.isRunAlways());
        assertEquals(DefaultProfiledChangerLog.class.getMethod("defaultProfiled"), changeSet.getMethod());
        List<String> profiles = Arrays.asList(changeSet.getMethod().getAnnotation(Profile.class).value());
        assertEquals(1, profiles.size());
        assertTrue(profiles.contains("default"));

        changeSet = changeLog.getChangeSetElements().get(1);
        assertEquals("no-profiled", changeSet.getId());
        assertEquals("testuser", changeSet.getAuthor());
        assertFalse(changeSet.isRunAlways());
        assertEquals(DefaultProfiledChangerLog.class.getMethod("noProfiled"), changeSet.getMethod());
        assertNull(changeSet.getMethod().getAnnotation(Profile.class));
    }


}
