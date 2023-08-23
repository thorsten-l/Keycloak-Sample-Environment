from django import template

from django.contrib.auth.models import User

register = template.Library()

@register.filter(name="is_local_user")
def is_local_user(user_pk):
    user = User.objects.get(pk=user_pk)
    if user.has_usable_password():
        return True
    else:
        return False

