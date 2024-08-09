package com.ebay.magellan.tascreed.core.domain.ban;

import lombok.Setter;

import java.util.Map;

@Setter
public class BanContext {
    private BanLevelEnum banGlobal;
    private Map<String, BanLevelEnum> banJobDefines;
    private Map<String, BanLevelEnum> banJobs;
    private Map<String, BanLevelEnum> banRoutineDefines;
    private Map<String, BanLevelEnum> banRoutines;

    // -----

    boolean isBanned(BanLevelEnum banLevel, BanLevelEnum needToBanLevel) {
        if (!BanLevelEnum.bannable(banLevel)) return false;
        return banLevel.covers(needToBanLevel);
    }

    public boolean isGlobalBanned(BanLevelEnum needToBanLevel) {
        return isBanned(banGlobal, needToBanLevel);
    }

    public boolean isJobDefineBanned(String jobDefineBannedKey, BanLevelEnum needToBanLevel) {
        if (banJobDefines == null) return false;
        BanLevelEnum bl = banJobDefines.get(jobDefineBannedKey);
        return isBanned(bl, needToBanLevel);
    }

    public boolean isJobBanned(String jobBannedKey, BanLevelEnum needToBanLevel) {
        if (banJobs == null) return false;
        BanLevelEnum bl = banJobs.get(jobBannedKey);
        return isBanned(bl, needToBanLevel);
    }

    public boolean isRoutineDefineBanned(String routineDefineBannedKey, BanLevelEnum needToBanLevel) {
        if (banRoutineDefines == null) return false;
        BanLevelEnum bl = banRoutineDefines.get(routineDefineBannedKey);
        return isBanned(bl, needToBanLevel);
    }

    public boolean isRoutineBanned(String routineBannedKey, BanLevelEnum needToBanLevel) {
        if (banRoutines == null) return false;
        BanLevelEnum bl = banRoutines.get(routineBannedKey);
        return isBanned(bl, needToBanLevel);
    }
}
