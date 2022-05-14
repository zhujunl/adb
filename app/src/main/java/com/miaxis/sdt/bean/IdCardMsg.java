package com.miaxis.sdt.bean;



/**
 * @author Tank
 * @date 2020/8/17 13:40
 * @des
 * @updateAuthor
 * @updateDes
 */
public class IdCardMsg  {

    public String name;
    public String sex;
    public String nation_str;


    public String birth_year ;
    public String birth_month ;
    public String birth_day ;
    public String address ;
    public String id_num ;
    public String sign_office;

    public String useful_s_date_year ;
    public String useful_s_date_month ;
    public String useful_s_date_day ;

    public String useful_e_date_year ;
    public String useful_e_date_month;
    public String useful_e_date_day ;


    public String strFp0;
    public String strFp1;
    public String strPH;
    
    public String type;
    
    public String chinesename;
    
    public String passnum;
    
    public String issueCount;

    public String version;

    public String getBrithday(){
        return  this.birth_year + "-" + this.birth_month + "-" + this.birth_day ;
    }

    public String getValidate(){
        return this.useful_s_date_year +  this.useful_s_date_month +  this.useful_s_date_day + "-" +this.useful_e_date_year+ this.useful_e_date_month  + this.useful_e_date_day ;
    }

//    @Override
//    public String toString() {
//        return "姓名:" + this.name + '\n'
//                +"类型"+ this.type + '\n'
//                + "性别:" + this.sex + '\n'
//                + "民族:" + this.nation_str + "族" + '\n'
//                + "出生日期:" + this.birth_year + "-" + this.birth_month + "-" + this.birth_day + '\n'
//                + "住址:" + this.address + '\n'
//                + "身份证号码:" + this.id_num + '\n'
//                + "签发机关:" + this.sign_office + '\n'
//                + "有效期起始日期:" + this.useful_s_date_year + "-" + this.useful_s_date_month + "-" + this.useful_s_date_day + '\n'
//                + "有效期截止日期:" + this.useful_e_date_year + "-" + this.useful_e_date_month + "-" + this.useful_e_date_day + '\n'
//                + "FP0"+strFp0+"\n"
//                + "FP2"+strFp1;
//    }


    @Override
    public String toString() {
        return "IdCardMsg{" +
                "姓名='" + name + '\n' +
                ", 性别='" + sex + '\n' +
                ", 民族='" + nation_str + "族" + '\n' +
                ", 出生日期='" + this.birth_year + "-" + this.birth_month + "-" + this.birth_day + '\n' +
                ", 住址='" + address + '\n' +
                ", 身份证号码='" + id_num + '\n' +
                ", 签发机关='" + sign_office + '\n' +
                ",有效期起始日期:" + this.useful_s_date_year + "-" + this.useful_s_date_month + "-" + this.useful_s_date_day + '\n'+
                ",有效期截止日期:" + this.useful_e_date_year + "-" + this.useful_e_date_month + "-" + this.useful_e_date_day + '\n'+
                ", strFp0='" + strFp0 + '\n' +
                ", strFp1='" + strFp1 + '\n' +
                ", strPH='" + strPH + '\n' +
                ", 类型='" + type + '\n' +
                ", 中文名='" + chinesename + '\n' +
                ", 通行证号码='" + passnum + '\n' +
                ", 签发次数='" + issueCount + '\n' +
                ", 证件版本号='" + version + '\n' +
                '}';
    }
}
