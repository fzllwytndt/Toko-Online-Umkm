import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
}

// Alamat backend dibaca dari local.properties, bukan ditulis di dalam kode.
// Alasannya: IP komputer berbeda pada tiap mesin dan berubah setiap ganti
// jaringan Wi-Fi, jadi kalau ditulis di kode nilainya ikut ter-commit lalu
// basi di komputer lain. local.properties sudah didaftarkan pada .gitignore.
//
// Isi local.properties, contoh:
//   server.url=http://192.168.1.5:3000/
//
// Kalau baris itu tidak ada, dipakai 10.0.2.2 yang cocok untuk Android Emulator.
val alamatServer: String = run {
    val berkas = rootProject.file("local.properties")
    val isi = Properties()

    if (berkas.exists()) {
        berkas.inputStream().use { isi.load(it) }
    }

    val nilai = isi.getProperty("server.url")?.trim().orEmpty()
    val alamat = nilai.ifEmpty { "http://10.0.2.2:3000/" }

    // Retrofit mewajibkan baseUrl diakhiri garis miring.
    if (alamat.endsWith("/")) alamat else "$alamat/"
}

android {
    namespace = "com.faizalwiyatnodito.tokoonlineumkm"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.faizalwiyatnodito.tokoonlineumkm"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "SERVER_URL", "\"$alamatServer\"")
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    buildTypes {
        release {
            optimization {
                enable = true
                packageScope = setOf("androidx.**", "kotlin.**", "kotlinx.**")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)

    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp.logging.interceptor)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
}
