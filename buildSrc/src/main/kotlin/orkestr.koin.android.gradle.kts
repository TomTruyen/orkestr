import orkestr.libs

val libs = project.libs

dependencies {
    add("implementation", libs.findLibrary("koin.android").get())
    add("implementation", libs.findLibrary("koin.androidx.compose").get())
}
