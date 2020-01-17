package com.mishenka.notbasic.data.model.photo.network

class Photo(
    var id: Long?,
    var farm: Long?,
    var server: Long?,
    var secret: String?
) {
    fun constructURL() =
        "https://farm$farm.staticflickr.com/$server/${id}_$secret.png"
}