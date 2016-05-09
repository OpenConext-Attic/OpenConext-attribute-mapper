var AttributeMapperApp = {};

AttributeMapperApp.updateUrlParameter = function (uri, key, value) {
  var i = uri.indexOf('#');
  var hash = i === -1 ? '' : uri.substr(i);
  uri = i === -1 ? uri : uri.substr(0, i);

  var re = new RegExp('([?&])' + key + '=.*?(&|$)', 'i');
  var separator = uri.indexOf('?') !== -1 ? '&' : '?';

  if (!value) {
    uri = uri.replace(new RegExp('([&]?)' + key + '=.*?(&|$)', 'i'), '');
    if (uri.slice(-1) === '?') {
      uri = uri.slice(0, -1);
    }
  } else if (uri.match(re)) {
    uri = uri.replace(re, '$1' + key + '=' + value + '$2');
  } else {
    uri = uri + separator + key + '=' + value;
  }
  return uri + hash;
};

AttributeMapperApp.changeLanguage = function (e, value) {
  if (e !== undefined && e !== null) {
    e.preventDefault();
    e.stopPropagation();

    if (!e.target.classList.contains('active')) {
      window.location.href = AttributeMapperApp.updateUrlParameter(window.location.href, 'lang', value);
    }
  }
};

document.addEventListener('DOMContentLoaded', function (event) {
  document.getElementById('language_en').addEventListener('click', function (e) {
    AttributeMapperApp.changeLanguage(e, 'en');
  });
  document.getElementById('language_nl').addEventListener('click', function (e) {
    AttributeMapperApp.changeLanguage(e, 'nl');
  });

});

