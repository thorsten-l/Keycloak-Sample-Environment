from django.shortcuts import render
from django.contrib.auth.decorators import login_required

from .models import *

# Create your views here.
def index(request):

    return render(request, 'app_app7/index.html')


@login_required
def protected(request):

    return render(request, 'app_app7/protected.html')