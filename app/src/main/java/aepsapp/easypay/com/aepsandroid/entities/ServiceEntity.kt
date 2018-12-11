package aepsapp.easypay.com.aepsandroid.entities

import java.io.Serializable

/**
 * Created by Viral on 14-03-2018.
 */
class ServiceEntity : Serializable {
    var createdDt: String? = null
    var updatedDt: String? = null
    var createdBy: String? = null
    var updatedBy: String? = null
    var status: Int = 0
    var versionNo: String? = null
    var olderStatus: String? = null
    var catMstId: Int = 0
    var category: String? = null
    var subcategory: String? = null
    var categoryCode: String? = null
    var subCategoryCode: String? = null
    var serviceType: String? = null
    var isBBPSOnly: String? = null
    var imgRes: Int = 0
    var imagePath:String?=null

    constructor()
    constructor(subCategory: String, imgRes: Int) {
        this.subcategory = subCategory
        this.imgRes = imgRes
    }
}