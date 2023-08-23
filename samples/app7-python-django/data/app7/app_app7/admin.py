from django.contrib import admin

# Register your models here.

from .models import *

class UserProfileAdmin(admin.ModelAdmin):
    pass

admin.site.register(UserProfile, UserProfileAdmin)