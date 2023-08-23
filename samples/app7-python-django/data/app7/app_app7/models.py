from django.db import models
from django.contrib.auth.models import User

# Create your models here.

class UserProfile(models.Model):
    user = models.OneToOneField(User, on_delete=models.CASCADE)
    userinfo = models.JSONField(blank=True, null=True)
    idtoken = models.JSONField(blank=True, null=True)
    accesstoken = models.JSONField(blank=True, null=True)

    def __str__(self):
        return "{0}".format(self.user.username)