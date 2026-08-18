import * as THREE from "three";

const container = document.getElementById("hero3d");
if (container) {
  const scene = new THREE.Scene();
  const camera = new THREE.PerspectiveCamera(45, container.clientWidth / container.clientHeight, 0.1, 100);
  camera.position.z = 4.2;

  const renderer = new THREE.WebGLRenderer({ antialias: true, alpha: true });
  renderer.setSize(container.clientWidth, container.clientHeight);
  renderer.setPixelRatio(Math.min(window.devicePixelRatio, 2));
  container.appendChild(renderer.domElement);

  const geometry = new THREE.IcosahedronGeometry(1.4, 1);
  const material = new THREE.MeshStandardMaterial({
    color: 0x00ffaa,
    metalness: 0.3,
    roughness: 0.35,
    wireframe: false,
  });
  const mesh = new THREE.Mesh(geometry, material);
  scene.add(mesh);

  const wireGeometry = new THREE.IcosahedronGeometry(1.55, 1);
  const wireMaterial = new THREE.MeshBasicMaterial({ color: 0x7c3aed, wireframe: true, transparent: true, opacity: 0.5 });
  scene.add(new THREE.Mesh(wireGeometry, wireMaterial));

  scene.add(new THREE.AmbientLight(0xffffff, 0.5));
  const point = new THREE.PointLight(0x00ffaa, 2.2);
  point.position.set(3, 3, 3);
  scene.add(point);

  function animate() {
    requestAnimationFrame(animate);
    mesh.rotation.x += 0.003;
    mesh.rotation.y += 0.004;
    renderer.render(scene, camera);
  }
  animate();

  window.addEventListener("resize", () => {
    camera.aspect = container.clientWidth / container.clientHeight;
    camera.updateProjectionMatrix();
    renderer.setSize(container.clientWidth, container.clientHeight);
  });
}
