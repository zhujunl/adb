package com.miaxis.sdt.callback;


import com.miaxis.sdt.bean.IdCard;

/**
 * @author Tank
 * @date 2021/4/29 10:33 AM
 * @des
 * @updateAuthor
 * @updateDes
 */

public interface ReadIdCardCallback {

    /**
     * 读身份证回调
     */
    void onIdCardRead(IdCard result);


}
