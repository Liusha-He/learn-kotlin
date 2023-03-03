package com.example.models

import com.fasterxml.jackson.annotation.JsonProperty

data class User(
    @JsonProperty("name") val name: String,
    @JsonProperty("age") val age: Int,
    @JsonProperty("countryOfResidence") val countryOfResidence: String
)
