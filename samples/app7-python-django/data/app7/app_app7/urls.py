from django.urls import path

from app_app7.views import index, protected

app_name = 'app_app7'
urlpatterns = [
    path("", index, name="index"),
    path("protected", protected, name="protected"),
]