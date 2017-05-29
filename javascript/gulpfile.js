'use strict';

var path = require('path');
var gulp = require('gulp');
var mkdir = require('mkdirp');
var browserify = require('gulp-browserify');
var rename = require('gulp-rename');

var handlebars = require('gulp-compile-handlebars');
var pkg = path.join(__dirname, 'package.json');
var version = require(pkg).version;

var PROD = (process.env.NODE_ENV === 'production');
var sourceDirectory = path.join(__dirname, 'src');
var buildDirectory = path.join(__dirname, PROD ? 'dist' : 'build', 'red5-js-bridge-' + version);

var defaultOptions = {
  
}

mkdir.sync(buildDirectory);

gulp.task('browserify-dependencies', function (cb) {
 
    gulp.src('./src/client/index.js')
        .pipe(browserify({
          insertGlobals : true
        }))
        .pipe(gulp.dest(buildDirectory))
        .on('end', cb);
});


gulp.task('compile', function (cb) {

  gulp.src(path.join(sourceDirectory, 'client', '**', '*.html'))
    .pipe(handlebars({
      version: version
    }, defaultOptions))
    .pipe(gulp.dest(buildDirectory))
    .on('end', cb);

});

gulp.task('move-scripts', ['compile'], function (cb) {

  gulp.src(path.join(sourceDirectory, 'client', '**', '*.js'))
    .pipe(gulp.dest(buildDirectory))
    .on('end', cb);

});


gulp.task('build', ['compile', 'browserify-dependencies', 'move-scripts'], function (cb) {
  cb();
});

