package br.com.bangu_ao_vivo.bangu.Model;

public class ModelEditProfile {

    String name, description, profissional, fun, interest, typeVerified;
    long verificate;

    public String getTypeVerified() {
        return typeVerified;
    }

    public void setTypeVerified(String typeVerified) {
        this.typeVerified = typeVerified;
    }

    public long getVerificate() {
        return verificate;
    }

    public void setVerificate(long verificate) {
        this.verificate = verificate;
    }

    public String getInterest() {
        return interest;
    }

    public void setInterest(String interest) {
        this.interest = interest;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProfissional() {
        return profissional;
    }

    public void setProfissional(String profissional) {
        this.profissional = profissional;
    }

    public String getFun() {
        return fun;
    }

    public void setFun(String fun) {
        this.fun = fun;
    }
}
