package br.com.bangu_ao_vivo.bangu.Model;

public class ModelReportUser {

    String uidSender, uidReported, motivation;
    boolean blockOption;

    public boolean isBlockOption() {
        return blockOption;
    }

    public void setBlockOption(boolean blockOption) {
        this.blockOption = blockOption;
    }

    public String getUidSender() {
        return uidSender;
    }

    public void setUidSender(String uidSender) {
        this.uidSender = uidSender;
    }

    public String getUidReported() {
        return uidReported;
    }

    public void setUidReported(String uidReported) {
        this.uidReported = uidReported;
    }

    public String getMotivation() {
        return motivation;
    }

    public void setMotivation(String motivation) {
        this.motivation = motivation;
    }
}
