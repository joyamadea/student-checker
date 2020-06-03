package id.ac.umn.studentchecker;

public class Mhs {
    public String mNim,mNama,mAngkatan,mBio,mPic,mProdi,mId;

    public Mhs() {

    }

    public Mhs(String mNim, String mNama, String mAngkatan, String mBio, String mPic, String mProdi,String mId) {
        this.mNim = mNim;
        this.mNama = mNama;
        this.mAngkatan = mAngkatan;
        this.mBio = mBio;
        this.mPic = mPic;
        this.mProdi = mProdi;
        this.mId = mId;
    }

    public Mhs(String mNim, String mNama, String mPic,String mId) {
        this.mNim = mNim;
        this.mNama = mNama;
        this.mPic = mPic;
        this.mId = mId;
    }

    public String getId(){
        return this.mId;
    }

    public void setId(String mId){
        this.mId = mId;
    }

    public String getNim(){
        return this.mNim;
    }

    public void setNim(String mNim){
        this.mNim = mNim;
    }

    public String getNama(){
        return this.mNama;
    }

    public void setNama(String mNama){
        this.mNama = mNama;
    }

    public String getAngkatan(){
        return this.mAngkatan;
    }

    public void setAngkatan(String mAngkatan){
        this.mAngkatan = mAngkatan;
    }

    public String getBio(){
        return this.mBio;
    }

    public void setBio(String mBio){
        this.mBio = mBio;
    }

    public String getPic(){
        return this.mPic;
    }

    public void setPic(String mPic){
        this.mPic = mPic;
    }

    public String getProdi(){
        return this.mProdi;
    }

    public void setProdi(String mProdi){
        this.mProdi = mProdi;
    }
}
