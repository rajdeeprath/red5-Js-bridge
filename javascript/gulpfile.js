'use strict';

var path = require('path');
var gulp = require('gulp');
var mkdir = require('mkdirp');
var browserify = require('gulp-browserify');
var rename = require('gulp-rename');
var babel = require("gulp-babel");
var pkg = path.join(__dirname, 'package.json');
var version = require(pkg).version;

var PROD = (process.env.NODE_ENV === 'production');
var sourceDirectory = path.join(__dirname, 'src');
var buildDirectory = path.join(__dirname, PROD ? 'dist' : 'build', 'red5-js-bridge-' + version);
var libraryBuildirectory = path.join(buildDirectory, 'lib');

var defaultOptions = {
  
}

mkdir.sync(buildDirectory);


gulp.task('babelify', function (cb) {
    gulp.src('./src/client/red5js.js')
        .pipe(babel())
        .pipe(gulp.dest(libraryBuildirectory))
        .on('end', cb);
});


gulp.task('browserify-dependencies', function (cb) {
 
    gulp.src('./src/client/red5js.js')
        .pipe(babel())
        .pipe(browserify({
          insertGlobals : true
        }))
        .pipe(gulp.dest(libraryBuildirectory))
        .on('end', cb);
});



gulp.task('move-scripts', function (cb) {

  gulp.src(path.join(sourceDirectory, 'client', '**', '*.js'))
    .pipe(gulp.dest(libraryBuildirectory))
    .on('end', cb);

});


gulp.task('build', ['babelify', 'browserify-dependencies', 'move-scripts'], function (cb) {
  cb();
});

