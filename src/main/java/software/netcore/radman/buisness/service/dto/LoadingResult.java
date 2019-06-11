package software.netcore.radman.buisness.service.dto;

import lombok.Getter;

/**
 *
 */
@Getter
public class LoadingResult {

    private int loaded = 0;
    private int duplicate = 0;
    private int errored = 0;

    public void incrementLoaded() {
        loaded++;
    }

    public void incrementDuplicate() {
        duplicate++;
    }

    public void incrementErrored() {
        errored++;
    }

}
