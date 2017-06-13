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
var examplesDirectory = path.join(__dirname, 'src', 'client', 'examples', '*');
var buildDirectory = path.join(__dirname, PROD ? 'dist' : 'build', 'red5js-' + version);
var libraryBuildirectory = path.join(buildDirectory, 'lib');
var npmBuildirectory = path.join(buildDirectory, 'bin');
var examplesBuildDirectory = path.join(buildDirectory, 'examples');

var defaultOptions = {
  
}

mkdir.sync(buildDirectory);
mkdir.sync(npmBuildirectory);



gulp.task('babelify', function (cb) {
    gulp.src('./src/client/red5js.js')
        .pipe(babel())
        .pipe(gulp.dest(libraryBuildirectory))
        .on('end', cb);
});


gulp.task('babelify-npm', function (cb) {
    gulp.src('./src/client/red5js.js')
        .pipe(babel())
        .pipe(gulp.dest(npmBuildirectory))
        .on('end', cb);
});


gulp.task('browserify-dependencies', function (cb) {    
    gulp.src('./src/client/red5js.js')
        .pipe(browserify({
          insertGlobals : true
        }))
        .pipe(gulp.dest(libraryBuildirectory))
        .on('end', cb);
});



gulp.task('move-script', function (cb) {
  gulp.src(path.join(sourceDirectory, 'client', 'red5js.js'))
    .pipe(gulp.dest(libraryBuildirectory))
    .on('end', cb);

});


gulp.task('move-examples', function (cb) {
  gulp.src(['./src/client/examples/*/*.*'])
    .pipe(gulp.dest(examplesBuildDirectory))
    .on('end', cb);
});


gulp.task('build', ['babelify', 'browserify-dependencies', 'move-script', 'move-examples'], function (cb) {
  cb();
});

gulp.task('build-npm', ['babelify-npm', 'move-script'], function (cb) {
  cb();
});
